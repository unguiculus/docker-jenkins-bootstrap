package action

import java.nio.file.Path

/**
 * Performs some Jenkins configuration action.
 */
abstract class ConfigAction {
    protected final Object config
    protected final Path bootstrapDir

    /**
     * @param bootstrapDir the directory which the JSON config was loaded from
     * @param jsonConfig the specific JSON entry for this class
     */
    ConfigAction(Path bootstrapDir, Object config) {
        this.bootstrapDir = bootstrapDir
        this.config = Objects.requireNonNull(config, "'config' must not be null")
    }

    abstract void execute()
}
