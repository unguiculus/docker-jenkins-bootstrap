package action

import groovy.transform.InheritConstructors
import jenkins.model.JenkinsLocationConfiguration

@InheritConstructors
class LocationConfigAction extends ConfigAction {

    @Override
    void execute() {
        def locationConfig = JenkinsLocationConfiguration.get()
        locationConfig.setUrl(config.url)
        locationConfig.setAdminAddress(config.adminAddress)
        locationConfig.save()
    }
}
