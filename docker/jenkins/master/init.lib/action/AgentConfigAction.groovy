package action

import groovy.transform.InheritConstructors
import hudson.model.Slave
import hudson.plugins.sshslaves.SSHLauncher
import hudson.plugins.sshslaves.verifiers.NonVerifyingKeyVerificationStrategy
import hudson.slaves.ComputerLauncher
import hudson.slaves.DumbSlave
import jenkins.model.Jenkins

@InheritConstructors
class AgentConfigAction extends ConfigAction {

    @Override
    void execute() {
        String hostName = config.hostName
        String credentialsId = config.credentialsId
        String name = config.name
        String remoteRootDir = config.remoteRootDir
        String description = config.description
        int numExecutors = config.numExecutors as int

        ComputerLauncher launcher = new SSHLauncher(hostName, 22, credentialsId, null, null, null, null, null, null,
            null, new NonVerifyingKeyVerificationStrategy())

        Slave agent = new DumbSlave(name, remoteRootDir, launcher)
        agent.setNodeDescription(description)
        agent.setNumExecutors(numExecutors)

        def instance = Jenkins.get()
        instance.addNode(agent)
        instance.save()
    }
}
