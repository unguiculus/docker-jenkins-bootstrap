package action

import groovy.transform.InheritConstructors
import hudson.util.StreamTaskListener
import org.jenkinsci.plugins.pipeline.maven.GlobalPipelineMavenConfig
import org.jenkinsci.plugins.pipeline.maven.MavenPublisher

@InheritConstructors
class MavenPipelineConfigAction extends ConfigAction {

    @Override
    void execute() {
        def mavenPublishers = MavenPublisher.buildPublishersList([], new StreamTaskListener(System.out)).findAll {
            config.disabledPublishers.contains(it.getClass().getName())
        }.collect {
            it.setDisabled(true)
            return it
        }

        GlobalPipelineMavenConfig mavenConfig = GlobalPipelineMavenConfig.get();
        mavenConfig.setPublisherOptions(mavenPublishers)
        mavenConfig.save()
    }
}
