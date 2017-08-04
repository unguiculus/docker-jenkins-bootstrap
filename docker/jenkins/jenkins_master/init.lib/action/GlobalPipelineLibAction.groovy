package action

import groovy.transform.InheritConstructors
import jenkins.plugins.git.GitSCMSource
import jenkins.scm.api.SCMSource
import org.jenkinsci.plugins.workflow.libs.GlobalLibraries
import org.jenkinsci.plugins.workflow.libs.LibraryConfiguration
import org.jenkinsci.plugins.workflow.libs.SCMSourceRetriever

@InheritConstructors
class GlobalPipelineLibAction extends ConfigAction {

    @Override
    void execute() {
        SCMSource scmSource = new GitSCMSource(repo)
        scmSource.setCredentialsId(credentialId)
        scmSource.getTraits().add(new BranchDiscoveryTrait())
        SCMSourceRetriever sourceRetriever = new SCMSourceRetriever(scmSource)
        LibraryConfiguration libConfig = new LibraryConfiguration(name, sourceRetriever)
        libConfig.setAllowVersionOverride(allowVersionOverride)
        libConfig.setImplicit(implicit)
        libConfig.setDefaultVersion(defaultVersion)

        def globalLibraries = GlobalLibraries.get()
        globalLibraries.setLibraries([libConfig])
        globalLibraries.save()
    }
}
