package action

import groovy.transform.InheritConstructors
import hudson.tasks.Shell
import jenkins.model.Jenkins

@InheritConstructors
class ShellConfigAction extends ConfigAction {

    @Override
    void execute() {
        def instance = Jenkins.getInstance()
        def shell = instance.getDescriptorByType(Shell.DescriptorImpl)
        shell.setShell(config)
        shell.save()
    }
}
