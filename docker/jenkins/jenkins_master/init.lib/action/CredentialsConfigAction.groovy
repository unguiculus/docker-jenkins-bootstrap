package action

import com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey
import com.cloudbees.plugins.credentials.CredentialsProvider
import com.cloudbees.plugins.credentials.CredentialsScope
import com.cloudbees.plugins.credentials.CredentialsStore
import com.cloudbees.plugins.credentials.SecretBytes
import com.cloudbees.plugins.credentials.SystemCredentialsProvider
import com.cloudbees.plugins.credentials.domains.Domain
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl
import groovy.transform.InheritConstructors
import hudson.security.ACL
import hudson.util.Secret
import jenkins.model.Jenkins
import org.jenkinsci.plugins.plaincredentials.impl.FileCredentialsImpl
import org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl

import java.nio.charset.StandardCharsets
import java.nio.file.Path

@InheritConstructors
class CredentialsConfigAction extends ConfigAction {

    @Override
    void execute() {
        CredentialsStore credentialsStore = SystemCredentialsProvider.getInstance().getStore()
        config.each { id, localConfig ->
            def cred

            switch (localConfig.type) {
                case 'SSH_KEY':
                    String fileName = localConfig.privateKeyFile
                    Path keyFile = bootstrapDir.resolve(fileName)
                    cred = new BasicSSHUserPrivateKey(CredentialsScope.GLOBAL, id, localConfig.username,
                        new BasicSSHUserPrivateKey.DirectEntryPrivateKeySource(keyFile.text), localConfig.passphrase,
                        localConfig.description)
                    break
                case 'USERNAME_PASSWORD':
                    cred = new UsernamePasswordCredentialsImpl(CredentialsScope.GLOBAL, id, localConfig.description,
                        localConfig.username, localConfig.password)
                    break
                case 'SECRET_TEXT':
                    String secretText = localConfig.secretText
                    if (!secretText) {
                        String fileName = localConfig.secretFile
                        secretText = bootstrapDir.resolve(fileName).getText(StandardCharsets.UTF_8.name())
                    }
                    cred = new StringCredentialsImpl(CredentialsScope.GLOBAL, id, localConfig.description,
                        Secret.fromString(secretText))
                    break
                case 'SECRET_FILE':
                    String fileName = localConfig.secretFile
                    byte[] secretFile = bootstrapDir.resolve(fileName).getBytes()
                    cred = new FileCredentialsImpl(CredentialsScope.GLOBAL, id, localConfig.description,
                        fileName, SecretBytes.fromBytes(secretFile))
                    break
                default:
                    throw new IllegalStateException("Unknown credentials type: ${localConfig.type}")
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
}
