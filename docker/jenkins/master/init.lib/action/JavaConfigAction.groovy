package action

import groovy.transform.InheritConstructors
import hudson.model.JDK
import jenkins.model.Jenkins

@InheritConstructors
class JavaConfigAction extends ConfigAction {

    @Override
    void execute() {
        config.each { String name, String javaHome ->
            JDK jdk = new JDK(name, javaHome)
            def descriptor = Jenkins.get().getDescriptorByType(JDK.DescriptorImpl)
            descriptor.setInstallations(jdk)
            descriptor.save()
        }
    }
}
