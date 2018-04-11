package action

import groovy.transform.InheritConstructors
import hudson.tasks.Maven
import jenkins.model.Jenkins

@InheritConstructors
class MavenConfigAction extends ConfigAction {

    @Override
    void execute() {
        def instance = Jenkins.get()
        def mavenTask = instance.getDescriptorByType(Maven.DescriptorImpl)
        mavenTask.setInstallations(new Maven.MavenInstallation(config.name, config.mavenHome, []))
        mavenTask.save()
    }
}
