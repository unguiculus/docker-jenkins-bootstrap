import action.ConfigAction

import java.nio.file.Path
import java.nio.file.Paths

/**
 * Bootstraps a Jenkins instance delegating to {@link ConfigAction} implementations in the {@code action} sub-package.
 */
class JenkinsBootstrapper {
    private static final Path JENKINS_HOME = Paths.get(System.getenv('JENKINS_HOME'))

    private final Path bootstrapDir
    private final Object config

    /**
     * @param bootstrapDir the directory which the JSON config was loaded from
     * @param config the JSON config
     */
    JenkinsBootstrapper(Path bootstrapDir, Object config) {
        this.bootstrapDir = Objects.requireNonNull(bootstrapDir, "'bootstrapDir' must not be null")
        this.config = Objects.requireNonNull(config, "'config' must not be null")
    }

    /**
     * Iterates over entries in the JSON object specified in the constructor. For each entry, it tries to instantiate
     * and execute an implementation of {@link ConfigAction}, the class name being the capitalized entry key
     * concatenated with 'ConfigAction'. The action class is initialized with this entry. A warning is logged to stderr
     * if the class is not found.
     */
    def execute() {
        config.each { key, localConfig ->
            String actionClassName = "action.${key.capitalize()}ConfigAction"
            try {
                println ">> Executing '$actionClassName'..."

                Class<? extends ConfigAction> actionClass = Class.forName(actionClassName).asSubclass(ConfigAction)
                ConfigAction action = actionClass.newInstance([bootstrapDir, localConfig] as Object[])
                action.execute()

                println "<< Finished executing '$actionClassName'."
                println ''
            } catch (ClassNotFoundException ex) {
                System.err.println "--- '$actionClassName' not found. Skipping config key '$key'..."
            }
        }
    }
}
