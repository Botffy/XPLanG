package ppke.itk.xplang.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Simple record holding information about the build.
 */
public final class VersionInfo {
    private static final Logger log = LoggerFactory.getLogger("Root.System.VersionInfo");

    /** The git commit hash */
    public final String version;

    /** Build date */
    public final String builtAt;

    /** Name of the user starting the build */
    public final String builtBy;

    /** The version of the JDK this was built with */
    public final String buildJDK;


    public VersionInfo() {
        Properties props = new Properties();
        try(InputStream stream = VersionInfo.class.getResourceAsStream("/build.properties")) {
            props.load(stream);
            log.debug("Loaded version.properties");
        } catch(IOException | NullPointerException e) {
            log.error("Failed to get version.properties. Exception: ", e);
        }

        this.version = props.getProperty("version", "unknown");
        this.builtAt = props.getProperty("builtAt", "N/A");
        this.builtBy = props.getProperty("builtBy", "N/A");
        this.buildJDK = props.getProperty("buildJDK", "N/A");
    }

    /**
     * Describe the version
     * @return a human-readable string with information about the build.
     */
    public String describe() {
        return String.format("%s, built by %s at %s", version, builtBy, builtAt);
    }

    @Override public String toString() {
        return String.format("version %s, built by %s at %s, JDK %s", version, builtBy, builtAt, buildJDK);
    }
}
