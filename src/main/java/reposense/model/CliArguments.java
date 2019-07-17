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
    protected List<Format> formats;
    protected boolean isAutomaticallyLaunching;
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

    public List<Format> getFormats() {
        return formats;
    }

    public boolean isAutomaticallyLaunching() {
        return isAutomaticallyLaunching;
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
                && this.formats.equals(otherCliArguments.formats)
                && this.isAutomaticallyLaunching == otherCliArguments.isAutomaticallyLaunching
                && this.zoneId.equals(otherCliArguments.zoneId);
    }
}
