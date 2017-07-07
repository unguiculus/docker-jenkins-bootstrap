package action

import groovy.transform.InheritConstructors
import jenkins.model.Jenkins

import java.nio.file.Path
import java.nio.file.Paths

@InheritConstructors
class BasicStuffConfigAction extends ConfigAction {

    @Override
    void execute() {
        def jenkinsHome = Paths.get(System.getenv('JENKINS_HOME'))
        Path installStateFile = jenkinsHome.resolve('jenkins.install.UpgradeWizard.state')
        installStateFile.text = '2.0'

        def instance = Jenkins.getInstance()

        instance.setNumExecutors(0)
        instance.setNoUsageStatistics(true)
        instance.save()
    }
}
