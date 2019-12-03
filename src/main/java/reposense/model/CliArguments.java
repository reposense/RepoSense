package reposense.model;

import java.nio.file.Path;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Represents command line arguments user supplied when running the program.
 */
public abstract class CliArguments {
    protected Path outputFilePath;
    protected Date sinceDate;
    protected Date untilDate;
    protected boolean isSinceDateProvided;
    protected boolean isUntilDateProvided;
    protected List<FileType> formats;
    protected boolean isAutomaticallyLaunching;
    protected boolean isStandaloneConfigIgnored;
    protected ZoneId zoneId;

    public ZoneId getZoneId() {
        return zoneId;
    }

    public Path getOutputFilePath() {
        return outputFilePath;
    }

    public Date getSinceDate() {
        return sinceDate;
    }

    public Date getUntilDate() {
        return untilDate;
    }

    public boolean isSinceDateProvided() {
        return isSinceDateProvided;
    }

    public boolean isUntilDateProvided() {
        return isUntilDateProvided;
    }

    public List<FileType> getFormats() {
        return formats;
    }

    public boolean isAutomaticallyLaunching() {
        return isAutomaticallyLaunching;
    }

    public boolean isStandaloneConfigIgnored() {
        return isStandaloneConfigIgnored;
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (this == other) {
            return true;
        }

        // instanceof handles null
        if (!(other instanceof CliArguments)) {
            return false;
        }

        CliArguments otherCliArguments = (CliArguments) other;

        return this.outputFilePath.equals(otherCliArguments.outputFilePath)
                && this.sinceDate.equals(otherCliArguments.sinceDate)
                && this.untilDate.equals(otherCliArguments.untilDate)
                && this.isSinceDateProvided == otherCliArguments.isSinceDateProvided
                && this.isUntilDateProvided == otherCliArguments.isUntilDateProvided
                && this.formats.equals(otherCliArguments.formats)
                && this.isAutomaticallyLaunching == otherCliArguments.isAutomaticallyLaunching
                && this.isStandaloneConfigIgnored == otherCliArguments.isStandaloneConfigIgnored
                && this.zoneId.equals(otherCliArguments.zoneId);
    }
}
