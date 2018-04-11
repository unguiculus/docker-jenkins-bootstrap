package action

import groovy.transform.InheritConstructors
import hudson.plugins.git.GitSCM
import jenkins.model.Jenkins

@InheritConstructors
class GitConfigAction extends ConfigAction {

    @Override
    void execute() {
        GitSCM.DescriptorImpl descriptor = Jenkins.get().getDescriptorByType(GitSCM.DescriptorImpl)
        descriptor.setGlobalConfigName(config.name)
        descriptor.setGlobalConfigEmail(config.email)
        descriptor.save()
    }
}
