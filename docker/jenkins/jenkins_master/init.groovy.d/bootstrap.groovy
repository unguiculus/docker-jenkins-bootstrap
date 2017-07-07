import groovy.json.JsonSlurper

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

println ''
println '**************************************************************************************************************'
println 'Configuring Jenkins...'

def bootstrapDir = Paths.get('/etc/jenkins_bootstrap')
def bootstrapConfig = bootstrapDir.resolve('bootstrap.json')

if (!Files.exists(bootstrapConfig)) {

    println "'bootstrap.json' not found. Nothing to configure."

} else {

    println "Loading 'JenkinsBootstrapper.groovy'..."
    println ''

    def slurper = new JsonSlurper()
    def json = slurper.parse(bootstrapConfig.toFile(), 'UTF-8')

    def bootstrapperClassFile = Paths.get(System.getenv('JENKINS_HOME'), 'init.lib')
    def gcl = new GroovyClassLoader(getClass().getClassLoader())
    gcl.addURL(bootstrapperClassFile.toUri().toURL())
    def bootstrapperClass = gcl.loadClass('JenkinsBootstrapper')
    def bootstrapper = bootstrapperClass.newInstance([bootstrapDir, json] as Object[])
    bootstrapper.execute()

    println 'Deleting bootstrap files...'
    bootstrapDir.eachFile { Path p ->
        //Files.delete(p)
    }

}

println 'Finished configuring Jenkins.'
println '**************************************************************************************************************'
println ''
