package action

import groovy.transform.InheritConstructors
import hudson.markup.RawHtmlMarkupFormatter
import hudson.security.FullControlOnceLoggedInAuthorizationStrategy
import hudson.security.csrf.DefaultCrumbIssuer
import javaposse.jobdsl.plugin.GlobalJobDslSecurityConfiguration
import jenkins.CLI
import jenkins.model.GlobalConfiguration
import jenkins.model.Jenkins
import jenkins.security.UpdateSiteWarningsConfiguration
import jenkins.security.s2m.AdminWhitelistRule
import net.sf.json.JSONObject

@InheritConstructors
class SecurityConfigAction extends ConfigAction {

    @Override
    void execute() {
        def instance = Jenkins.get()

        def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
        strategy.setAllowAnonymousRead(false)
        instance.setAuthorizationStrategy(strategy)

        AdminWhitelistRule rule = instance.getExtensionList(AdminWhitelistRule)[0]
        rule.setMasterKillSwitch(false)

        instance.setCrumbIssuer(new DefaultCrumbIssuer(true))
        instance.setMarkupFormatter(new RawHtmlMarkupFormatter(false))

        def allConfigs = GlobalConfiguration.all()
        allConfigs.get(GlobalJobDslSecurityConfiguration).configure(null, new JSONObject())

        def json = new JSONObject()
        json.put('SECURITY-336', false) // Pipeline: Classpath Steps: Script Security sandbox bypass
        allConfigs.get(UpdateSiteWarningsConfiguration).configure(null, json)

        CLI.get().setEnabled(false)

        instance.save()
    }
}
