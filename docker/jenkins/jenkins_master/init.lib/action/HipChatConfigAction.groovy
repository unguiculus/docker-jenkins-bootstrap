package action

import groovy.transform.InheritConstructors
import jenkins.model.Jenkins
import jenkins.plugins.hipchat.HipChatNotifier

@InheritConstructors
class HipChatConfigAction extends ConfigAction {

    @Override
    void execute() {
        def instance = Jenkins.getInstance()
        HipChatNotifier.DescriptorImpl hipChatNotifier = instance.getDescriptorByType(HipChatNotifier.DescriptorImpl)
        hipChatNotifier.setServer(config.server)
        hipChatNotifier.setCredentialId(config.credentialId)
        hipChatNotifier.setRoom(config.room)
        hipChatNotifier.setSendAs(config.sendAs)
        hipChatNotifier.setV2Enabled(true)
        hipChatNotifier.save()
    }
}
