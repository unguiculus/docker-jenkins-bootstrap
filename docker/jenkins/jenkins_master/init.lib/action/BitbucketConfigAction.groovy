package action

import com.cloudbees.jenkins.plugins.bitbucket.endpoints.BitbucketCloudEndpoint
import com.cloudbees.jenkins.plugins.bitbucket.endpoints.BitbucketEndpointConfiguration
import groovy.transform.InheritConstructors
import jenkins.model.Jenkins

@InheritConstructors
class BitbucketConfigAction extends ConfigAction {

    @Override
    void execute() {
        def instance = Jenkins.getInstance()

        BitbucketCloudEndpoint endpoint = new BitbucketCloudEndpoint(true, credentialId)
        BitbucketEndpointConfiguration endpointConfig = instance.getDescriptorByType(BitbucketEndpointConfiguration)
        endpointConfig.updateEndpoint(endpoint)
        endpointConfig.save()
    }
}
