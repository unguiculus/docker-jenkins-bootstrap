package action

import groovy.transform.InheritConstructors
import hudson.plugins.locale.PluginImpl
import jenkins.model.Jenkins
import net.sf.json.JSONObject

@InheritConstructors
class LocaleConfigAction extends ConfigAction {

    @Override
    void execute() {
        def instance = Jenkins.get()
        def localePlugin = instance.getPlugin(PluginImpl)
        JSONObject json = new JSONObject()
        json.put('systemLocale', config.systemLocale)
        json.put('ignoreAcceptLanguage', config.ignoreAcceptLanguage)
        localePlugin.configure(null, json)
        localePlugin.save()
    }
}
