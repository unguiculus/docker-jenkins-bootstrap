package action

import groovy.transform.InheritConstructors
import hudson.plugins.sonar.SonarGlobalConfiguration
import hudson.plugins.sonar.SonarInstallation
import jenkins.model.Jenkins

import static hudson.plugins.sonar.utils.SQServerVersions.SQ_5_3_OR_HIGHER

@InheritConstructors
class SonarQubeConfigAction extends ConfigAction {

    @Override
    void execute() {
        def instance = Jenkins.get()

        SonarGlobalConfiguration sonar = instance.getDescriptorByType(SonarGlobalConfiguration)
        sonar.setBuildWrapperEnabled(true)

        SonarInstallation installation = new SonarInstallation('SonarQube', config.url,
            SQ_5_3_OR_HIGHER, null, null, null, null, null, null, null, null, null, null)
        sonar.setInstallations(installation)

        sonar.save()
    }
}
