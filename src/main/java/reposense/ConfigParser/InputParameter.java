package reposense.ConfigParser;

import java.io.File;
import java.util.Date;
import java.util.Optional;

public final class InputParameter {
    private static final String DEFAULT_FILE_ARG = ".";

    /**
     * Stores the csv file location
     */
    private File configFile;

    /**
     * Stores the output location
     */
    private File targetFile;

    /**
     * The date to start
     */
    private Optional<Date> sinceDate;

    /**
     * The date to stop
     */
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
