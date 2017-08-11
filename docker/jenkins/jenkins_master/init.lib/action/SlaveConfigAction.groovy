package action

import groovy.transform.InheritConstructors
import hudson.model.Node
import hudson.model.Slave
import hudson.plugins.sshslaves.SSHLauncher
import hudson.plugins.sshslaves.verifiers.NonVerifyingKeyVerificationStrategy
import hudson.slaves.DumbSlave
import hudson.slaves.RetentionStrategy
import jenkins.model.Jenkins

@InheritConstructors
class SlaveConfigAction extends ConfigAction {

    @Override
    void execute() {
        Slave slave = new DumbSlave(config.name, config.description, config.remoteRootDir, config.numExecutors,
            Node.Mode.NORMAL, null, new SSHLauncher(config.hostName, 22, config.credentialsId, null, null, null, null,
            null, null, null, new NonVerifyingKeyVerificationStrategy()), new RetentionStrategy.Always())
        def instance = Jenkins.getInstance()
        instance.addNode(slave)
        instance.save()
    }
}
