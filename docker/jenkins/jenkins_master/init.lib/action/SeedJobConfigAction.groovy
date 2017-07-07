package action

import groovy.transform.InheritConstructors
import javaposse.jobdsl.dsl.DslScriptLoader
import javaposse.jobdsl.plugin.JenkinsJobManagement

import java.nio.charset.StandardCharsets

@InheritConstructors
class SeedJobConfigAction extends ConfigAction {

    @Override
    void execute() {
        String jobDslFileName = config.jobDslFile
        String jobDsl = bootstrapDir.resolve(jobDslFileName).getText(StandardCharsets.UTF_8.name())
        def jobManagement = new JenkinsJobManagement(System.out, [:], new File('.'))
        new DslScriptLoader(jobManagement).runScript(jobDsl)
    }
}
