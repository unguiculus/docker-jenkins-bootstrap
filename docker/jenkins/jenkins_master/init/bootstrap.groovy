import groovy.json.JsonSlurper

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

println ''
println '********************************************************************************'
println 'Configuring Jenkins...'

def bootstrapDir = Paths.get('/etc/jenkins_bootstrap')
def bootstrapperClassFile = bootstrapDir.resolve('JenkinsBootstrapper.groovy')

if (!Files.exists(bootstrapperClassFile)) {

    println "'JenkinsBootstrapper.groovy' not found. Nothing to configure."

} else {

    println "Loading 'JenkinsBootstrapper.groovy'..."

    def bootstrapConfigPath = bootstrapDir.resolve('bootstrap.json')
    def slurper = new JsonSlurper()
    def json = slurper.parse(bootstrapConfigPath.toFile(), 'UTF-8')

    def gcl = new GroovyClassLoader(getClass().getClassLoader())
    def bootstrapperClass = gcl.parseClass(bootstrapperClassFile.toFile())
    def bootstrapper = bootstrapperClass.newInstance([bootstrapDir, json] as Object[])
    bootstrapper.execute()

    println 'Deleting bootstrap files...'
    bootstrapDir.eachFile { Path p ->
        Files.delete(p)
    }

}

println 'Finished configuring Jenkins.'
println '********************************************************************************'
println ''
