package action

import groovy.transform.InheritConstructors
import hudson.model.Slave
import hudson.plugins.sshslaves.SSHLauncher
import hudson.slaves.ComputerLauncher
import hudson.slaves.DumbSlave
import jenkins.model.Jenkins

@InheritConstructors
class SlaveConfigAction extends ConfigAction {

    @Override
    void execute() {
        String hostName = config.hostName
        String credentialsId = config.credentialsId
        String name = config.name
        String remoteRootDir = config.remoteRootDir
        String description = config.description
        int numExecutors = config.numExecutors as int

        ComputerLauncher launcher = new SSHLauncher(hostName, 22, credentialsId, null, null, null, null)

        Slave slave = new DumbSlave(name, remoteRootDir, launcher)
        slave.setNodeDescription(description)
        slave.setNumExecutors(numExecutors)

        def instance = Jenkins.getInstance()
        instance.addNode(slave)
        instance.save()
    }
}
