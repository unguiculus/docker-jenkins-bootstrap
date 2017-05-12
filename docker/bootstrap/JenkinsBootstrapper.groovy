import com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey
import com.cloudbees.plugins.credentials.CredentialsProvider
import com.cloudbees.plugins.credentials.CredentialsScope
import com.cloudbees.plugins.credentials.CredentialsStore
import com.cloudbees.plugins.credentials.SecretBytes
import com.cloudbees.plugins.credentials.SystemCredentialsProvider
import com.cloudbees.plugins.credentials.domains.Domain
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl
import de.theit.jenkins.crowd.CrowdSecurityRealm
import hudson.markup.RawHtmlMarkupFormatter
import hudson.model.JDK
import hudson.model.Slave
import hudson.plugins.git.GitSCM
import hudson.plugins.locale.PluginImpl
import hudson.plugins.sonar.SonarGlobalConfiguration
import hudson.plugins.sonar.SonarInstallation
import hudson.plugins.sshslaves.SSHLauncher
import hudson.plugins.sshslaves.verifiers.NonVerifyingKeyVerificationStrategy
import hudson.plugins.timestamper.TimestamperConfig
import hudson.security.ACL
import hudson.security.FullControlOnceLoggedInAuthorizationStrategy
import hudson.security.HudsonPrivateSecurityRealm
import hudson.security.csrf.DefaultCrumbIssuer
import hudson.slaves.DumbSlave
import hudson.slaves.RetentionStrategy
import hudson.tasks.Maven
import hudson.tasks.Shell
import hudson.util.Secret
import javaposse.jobdsl.dsl.DslScriptLoader
import javaposse.jobdsl.plugin.GlobalJobDslSecurityConfiguration
import javaposse.jobdsl.plugin.JenkinsJobManagement
import jenkins.CLI
import jenkins.model.GlobalConfiguration
import jenkins.model.Jenkins
import jenkins.model.JenkinsLocationConfiguration
import jenkins.mvn.GlobalMavenConfig
import jenkins.plugins.hipchat.HipChatNotifier
import jenkins.security.UpdateSiteWarningsConfiguration
import jenkins.security.s2m.AdminWhitelistRule
import net.sf.json.JSONObject
import org.jenkinsci.plugins.configfiles.GlobalConfigFiles
import org.jenkinsci.plugins.configfiles.maven.MavenSettingsConfig
import org.jenkinsci.plugins.configfiles.maven.job.MvnSettingsProvider
import org.jenkinsci.plugins.configfiles.maven.security.ServerCredentialMapping
import org.jenkinsci.plugins.plaincredentials.impl.FileCredentialsImpl
import org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl

import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.nio.file.Paths

import static hudson.model.Node.Mode
import static hudson.plugins.sonar.utils.SQServerVersions.SQ_5_3_OR_HIGHER

class JenkinsBootstrapper {
    private static final Path JENKINS_HOME = Paths.get(System.getenv('JENKINS_HOME'))

    private final Path bootstrapDir
    private final Object config

    JenkinsBootstrapper(Path bootstrapDir, Object config) {
        this.bootstrapDir = bootstrapDir
        this.config = config
    }

    def execute() {
        configureCredentials(config.credentials)

        configureBasicStuff()

        def location = config.location
        if (location) {
            configureLocation(location.url, location.adminAddress)
        }

        def security = config.security?.enabled
        if (security) {
            configureSecurity()
        }

        def initialUser = config.security?.initialUser
        if (initialUser) {
            configureInitialUser(initialUser.name, initialUser.password)
        }

        def crowd = config.security?.crowd
        if (crowd) {
            configureCrowd(crowd.url, crowd.application, crowd.password, crowd.group)
        }

        def git = config.git
        if (git) {
            configureGit(git.name, git.email)
        }

        def java = config.java
        if (java) {
            configureJdk(java.name, java.javaHome)
        }

        def maven = config.maven
        if (maven) {
            configureMaven(maven.name, maven.mavenHome)
        }

        def mavenSettings = config.mavenSettings
        if (mavenSettings) {
            List<ServerCredentialMapping> credentialMappings = mavenSettings.credentialMappings.collect { k, v ->
                return new ServerCredentialMapping(k, v)
            }
            configureMavenSettings(mavenSettings.id, mavenSettings.name, mavenSettings.settingsFile, credentialMappings)
        }

        def timestamper = config.timestamper
        if (timestamper) {
            configureTimestamper(timestamper.systemTimeFormat, timestamper.elapsedTimeFormat)
        }

        if (config.shell) {
            configureShell(config.shell)
        }

        def slave = config.slave
        if (slave) {
            configureSlave(slave.name, slave.sshKeyId, slave.description, slave.remoteRootDir, slave.numExecutors, slave.hostName)
        }

        def hcn = config.hipChatNotifier
        if (hcn) {
            configureHipChatNotifier(hcn.server, hcn.credentialId, hcn.room, hcn.sendAs)
        }

        def sonarQube = config.sonarQube
        if (sonarQube) {
            configureSonarQube(sonarQube.url)
        }

        def seedJob = config.seedJob
        if (seedJob) {
            createSeedJob(seedJob.jobDslFile)
        }
    }

    private def configureCredentials(Object credentialsConfig) {
        println 'Creating credentials...'

        CredentialsStore credentialsStore = SystemCredentialsProvider.getInstance().getStore()
        credentialsConfig.each { id, config ->
            println "Creating credentials (id=$id)..."
            def cred

            switch (config.type) {
                case 'SSH_KEY':
                    String fileName = config.privateKeyFile
                    Path keyFile = bootstrapDir.resolve(fileName)
                    cred = new BasicSSHUserPrivateKey(CredentialsScope.GLOBAL, id, config.username,
                        new BasicSSHUserPrivateKey.DirectEntryPrivateKeySource(keyFile.text), config.passphrase,
                        config.description)
                    break
                case 'USERNAME_PASSWORD':
                    cred = new UsernamePasswordCredentialsImpl(CredentialsScope.GLOBAL, id, config.description,
                        config.username, config.password)
                    break
                case 'SECRET_TEXT':
                    String secretText = config.secretText
                    if (!secretText) {
                        String fileName = config.secretFile
                        secretText = bootstrapDir.resolve(fileName).getText(StandardCharsets.UTF_8.name())
                    }
                    cred = new StringCredentialsImpl(CredentialsScope.GLOBAL, id, config.description,
                        Secret.fromString(secretText))
                    break
                case 'SECRET_FILE':
                    String fileName = config.secretFile
                    byte[] secretFile = bootstrapDir.resolve(fileName).getBytes()
                    cred = new FileCredentialsImpl(CredentialsScope.GLOBAL, id, config.description,
                        fileName, SecretBytes.fromBytes(secretFile))
                    break
                default:
                    throw new IllegalStateException("Unknown credentials type: ${config.type}")
            }

            def creds = CredentialsProvider.lookupCredentials(cred.class, Jenkins.getInstance(), ACL.SYSTEM, [])
            def oldCred = creds.findResult { it.id == cred.id ? it : null }

            if (oldCred) {
                credentialsStore.updateCredentials(Domain.global(), oldCred, cred)
            } else {
                credentialsStore.addCredentials(Domain.global(), cred)
            }
        }

        credentialsStore.save()
    }

    private def configureBasicStuff() {
        'Configuring basic stuff...'

        Path installStateFile = JENKINS_HOME.resolve('jenkins.install.UpgradeWizard.state')
        installStateFile.text = '2.0'

        def instance = Jenkins.getInstance()

        def localePlugin = instance.getPlugin(PluginImpl)
        JSONObject json = new JSONObject()
        json.put('systemLocale', 'en_US')
        json.put('ignoreAcceptLanguage', 'true')
        localePlugin.configure(null, json)
        localePlugin.save()

        instance.setNumExecutors(0)
        instance.setNoUsageStatistics(true)
        instance.save()
    }

    private def configureLocation(String url, String adminAddress) {
        println 'Configuring location...'

        def locationConfig = JenkinsLocationConfiguration.get()
        locationConfig.setUrl(url)
        locationConfig.setAdminAddress(adminAddress)
        locationConfig.save()
    }

    private def configureSecurity() {
        println 'Configuring security...'

        def instance = Jenkins.getInstance()

        def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
        strategy.setAllowAnonymousRead(false)
        instance.setAuthorizationStrategy(strategy)

        AdminWhitelistRule rule = instance.getExtensionList(AdminWhitelistRule)[0]
        rule.setMasterKillSwitch(false)

        instance.setCrumbIssuer(new DefaultCrumbIssuer(true))
        instance.setMarkupFormatter(new RawHtmlMarkupFormatter(false))

        def allConfigs = GlobalConfiguration.all()
        allConfigs.get(GlobalJobDslSecurityConfiguration).configure(null, new JSONObject())

        def json = new JSONObject()
        json.put('SECURITY-336', false) // Pipeline: Classpath Steps: Script Security sandbox bypass
        allConfigs.get(UpdateSiteWarningsConfiguration).configure(null, json)

        CLI.get().setEnabled(false)

        GlobalConfiguration.all().get(GlobalJobDslSecurityConfiguration.class).configure(null, new JSONObject())

        instance.save()
    }

    private def configureInitialUser(String user, String password) {
        println 'Configuring initial user...'

        def instance = Jenkins.getInstance()

        def hudsonRealm = new HudsonPrivateSecurityRealm(false)
        hudsonRealm.createAccount(user, password)
        instance.setSecurityRealm(hudsonRealm)
        instance.save()
    }

    private def configureCrowd(String crowdUrl, String crowdApplication, String crowdPassword, String crowdGroup) {
        println 'Configuring Crowd...'

        def instance = Jenkins.getInstance()

        def crowdRealm = new CrowdSecurityRealm(crowdUrl, crowdApplication, crowdPassword, crowdGroup,
            false, 2, false, null, null, false, null, null, null, null, null, null, null);
        instance.setSecurityRealm(crowdRealm)

        instance.save()
    }

    private def configureGit(String name, String email) {
        println "Configuring Git (name=$name, email=$email)..."

        GitSCM.DescriptorImpl descriptor = Jenkins.getInstance().getDescriptorByType(GitSCM.DescriptorImpl)
        descriptor.setGlobalConfigName(name)
        descriptor.setGlobalConfigEmail(email)
        descriptor.save()
    }

    private def configureJdk(String name, String javaHome) {
        println "Configuring JDK (name=$name, javaHome=$javaHome)..."

        JDK jdk = new JDK(name, javaHome)
        def descriptor = Jenkins.getInstance().getDescriptorByType(JDK.DescriptorImpl)
        descriptor.setInstallations(jdk)
        descriptor.save()
    }

    private def configureMaven(String name, String mavenHome) {
        println "Configuring Maven (name=$name, mavenHome=$mavenHome)..."

        def instance = Jenkins.getInstance()
        def mavenTask = instance.getDescriptorByType(Maven.DescriptorImpl)
        mavenTask.setInstallations(new Maven.MavenInstallation(name, mavenHome, []))
        mavenTask.save()
    }

    private def configureMavenSettings(String id, String name, String settingFileName, List<ServerCredentialMapping> credentialMappings) {
        println "Configuring Maven settings..."

        String settingFile = bootstrapDir.resolve(settingFileName).getText(StandardCharsets.UTF_8.name())
        MavenSettingsConfig mavenSettingsConfig = new MavenSettingsConfig(id, name, name, settingFile, true, credentialMappings)
        GlobalConfigFiles.get().save(mavenSettingsConfig)

        def mavenConfig = GlobalMavenConfig.get()
        mavenConfig.setSettingsProvider(new MvnSettingsProvider(id))
        mavenConfig.save()
    }

    private def configureTimestamper(String systemTimeFormat, String elapsedTimeFormat) {
        println "Configuring Maven (systemTimeFormat=$systemTimeFormat, elapsedTimeFormat=$elapsedTimeFormat)..."

        TimestamperConfig config = TimestamperConfig.get()
        config.setSystemTimeFormat(systemTimeFormat)
        config.setElapsedTimeFormat(elapsedTimeFormat)
        config.save()
    }

    private def configureShell(String path) {
        println "Configuring Shell (path=$path)..."

        def instance = Jenkins.getInstance()
        def shell = instance.getDescriptorByType(Shell.DescriptorImpl)
        shell.setShell(path)
        shell.save()
    }

    private def configureSlave(String name, String credentialsId, String description, String remoteRootDir,
            String numExecutors, String hostName) {
        println 'Configuring build slave...'

        Slave slave = new DumbSlave(name, description, remoteRootDir, numExecutors, Mode.NORMAL, null,
            new SSHLauncher(hostName, 22, credentialsId, null, null, null, null, null, null, null,
                new NonVerifyingKeyVerificationStrategy()), new RetentionStrategy.Always())
        def instance = Jenkins.getInstance()
        instance.addNode(slave)
        instance.save()
    }

    private def configureHipChatNotifier(String server, String credentialId, String room, String sendAs) {
        println 'Configuring HipChat notifier...'

        def instance = Jenkins.getInstance()
        HipChatNotifier.DescriptorImpl hipChatNotifier = instance.getDescriptorByType(HipChatNotifier.DescriptorImpl)
        hipChatNotifier.setServer(server)
        hipChatNotifier.setCredentialId(credentialId)
        hipChatNotifier.setRoom(room)
        hipChatNotifier.setSendAs(sendAs)
        hipChatNotifier.save()
    }

    private def configureSonarQube(String url) {
        println 'Configuring SonarQube...'

        def instance = Jenkins.getInstance()

        SonarGlobalConfiguration sonar = instance.getDescriptorByType(SonarGlobalConfiguration)
        sonar.setBuildWrapperEnabled(true)

        SonarInstallation installation = new SonarInstallation('SonarQube', url,
            SQ_5_3_OR_HIGHER, null, null, null, null, null, null, null, null, null, null)
        sonar.setInstallations(installation)

        sonar.save()
    }

    private def createSeedJob(String jobDslFileName) {
        println 'Creating seed job...'

        String jobDsl = bootstrapDir.resolve(jobDslFileName).getText(StandardCharsets.UTF_8.name())
        def jobManagement = new JenkinsJobManagement(System.out, [:], new File('.'))
        new DslScriptLoader(jobManagement).runScript(jobDsl)
    }
}
