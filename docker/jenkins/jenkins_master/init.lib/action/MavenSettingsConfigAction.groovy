package action

import groovy.transform.InheritConstructors
import jenkins.mvn.GlobalMavenConfig
import org.jenkinsci.plugins.configfiles.GlobalConfigFiles
import org.jenkinsci.plugins.configfiles.maven.MavenSettingsConfig
import org.jenkinsci.plugins.configfiles.maven.job.MvnSettingsProvider
import org.jenkinsci.plugins.configfiles.maven.security.ServerCredentialMapping

import java.nio.charset.StandardCharsets

@InheritConstructors
class MavenSettingsConfigAction extends ConfigAction {

    @Override
    void execute() {
        String settingsFileName = config.settingsFile
        String settingsFile = bootstrapDir.resolve(settingsFileName).getText(StandardCharsets.UTF_8.name())
        List<ServerCredentialMapping> credentialMappings = config.credentialMappings.collect { k, v ->
            return new ServerCredentialMapping(k, v)
        }

        MavenSettingsConfig mavenSettingsConfig = new MavenSettingsConfig(config.id, config.name, config.name,
            settingsFile, true, credentialMappings)
        GlobalConfigFiles.get().save(mavenSettingsConfig)

        def mavenConfig = GlobalMavenConfig.get()
        mavenConfig.setSettingsProvider(new MvnSettingsProvider(config.id))
        mavenConfig.save()
    }
}
