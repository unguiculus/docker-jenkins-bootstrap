package action

import groovy.transform.InheritConstructors
import jenkins.model.Jenkins

@InheritConstructors
class BasicStuffConfigAction extends ConfigAction {

    @Override
    void execute() {
        def instance = Jenkins.getInstance()

        instance.setNumExecutors(0)
        instance.setNoUsageStatistics(true)
        instance.save()
    }
}
