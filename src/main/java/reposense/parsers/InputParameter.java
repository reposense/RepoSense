package reposense.parsers;

import java.io.File;
import java.util.Date;
import java.util.Optional;

/**
 * Stores parsed user-supplied CLI arguments.
 */
public final class InputParameter {
    private static final String DEFAULT_FILE_ARG = ".";

    private File configFile;

    private File targetFile;

    private Optional<Date> sinceDate;

    private Optional<Date> untilDate;

    public InputParameter() {
        sinceDate = Optional.empty();
        untilDate = Optional.empty();
        targetFile = new File(DEFAULT_FILE_ARG);
    }

    public File getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = new File(configFile);
    }

    public Optional<Date> getSinceDate() {
        return sinceDate;
    }

    public File getTargetFile() {
        return targetFile;
    }

    public void setTargetFile(String targetFile) {
        this.targetFile = new File(targetFile);
    }

    public void setSinceDate(Date sinceDate) {
        this.sinceDate = Optional.ofNullable(sinceDate);
    }

    public Optional<Date> getUntilDate() {
        return untilDate;
    }

    public void setUntilDate(Date untilDate) {
        this.untilDate = Optional.ofNullable(untilDate);
    }
}
