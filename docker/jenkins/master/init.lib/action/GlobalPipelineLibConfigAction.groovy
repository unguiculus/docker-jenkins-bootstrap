package action

import groovy.transform.InheritConstructors
import jenkins.plugins.git.GitSCMSource
import jenkins.plugins.git.traits.BranchDiscoveryTrait
import jenkins.scm.api.SCMSource
import org.jenkinsci.plugins.workflow.libs.GlobalLibraries
import org.jenkinsci.plugins.workflow.libs.LibraryConfiguration
import org.jenkinsci.plugins.workflow.libs.SCMSourceRetriever

@InheritConstructors
class GlobalPipelineLibConfigAction extends ConfigAction {

    @Override
    void execute() {
        SCMSource scmSource = new GitSCMSource(config.repo)
        scmSource.setCredentialsId(config.credentialId)
        scmSource.getTraits().add(new BranchDiscoveryTrait())
        SCMSourceRetriever sourceRetriever = new SCMSourceRetriever(scmSource)
        LibraryConfiguration libConfig = new LibraryConfiguration(config.name, sourceRetriever)
        libConfig.setAllowVersionOverride(config.allowVersionOverride)
        libConfig.setImplicit(config.implicit)
        libConfig.setDefaultVersion(config.defaultVersion)

        def globalLibraries = GlobalLibraries.get()
        globalLibraries.setLibraries([libConfig])
        globalLibraries.save()
    }
}
