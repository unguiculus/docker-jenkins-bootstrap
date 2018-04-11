package action

import groovy.transform.InheritConstructors
import hudson.plugins.timestamper.TimestamperConfig

@InheritConstructors
class TimestamperConfigAction extends ConfigAction {

    @Override
    void execute() {
        TimestamperConfig tsConfig = TimestamperConfig.get()
        tsConfig.setSystemTimeFormat(config.systemTimeFormat)
        tsConfig.setElapsedTimeFormat(config.elapsedTimeFormat)
        tsConfig.save()
    }
}
