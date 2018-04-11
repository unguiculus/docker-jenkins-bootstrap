package action

import groovy.transform.InheritConstructors
import hudson.security.HudsonPrivateSecurityRealm
import jenkins.model.Jenkins

@InheritConstructors
class InitialUserConfigAction extends ConfigAction {

    @Override
    void execute() {
        def instance = Jenkins.get()
        def hudsonRealm = new HudsonPrivateSecurityRealm(false)
        hudsonRealm.createAccount(config.name, config.password)
        instance.setSecurityRealm(hudsonRealm)
        instance.save()
    }
}
