package reposense.model;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import reposense.parser.ArgsParser;

/**
 * Represents command line arguments user supplied when running the program.
 */
public abstract class CliArguments {
    protected Path outputFilePath;
    protected Path assetsFilePath;
    protected LocalDateTime sinceDate;
    protected LocalDateTime untilDate;
    protected boolean isSinceDateProvided;
    protected boolean isUntilDateProvided;
    protected List<FileType> formats;
    protected boolean isLastModifiedDateIncluded;
    protected boolean isShallowCloningPerformed;
    protected boolean isAutomaticallyLaunching;
    protected boolean isStandaloneConfigIgnored;
    protected boolean isFileSizeLimitIgnored;
    protected int numCloningThreads;
    protected int numAnalysisThreads;
    protected ZoneId zoneId;
    protected boolean isFindingPreviousAuthorsPerformed;
    protected boolean isTestMode = ArgsParser.DEFAULT_IS_TEST_MODE;
    protected boolean isFreshClonePerformed = ArgsParser.DEFAULT_SHOULD_FRESH_CLONE;

    public ZoneId getZoneId() {
        return zoneId;
    }

    public Path getOutputFilePath() {
        return outputFilePath;
    }

    public Path getAssetsFilePath() {
        return assetsFilePath;
    }

    public LocalDateTime getSinceDate() {
        return sinceDate;
    }

    public LocalDateTime getUntilDate() {
        return untilDate;
    }

    public boolean isSinceDateProvided() {
        return isSinceDateProvided;
    }

    public boolean isUntilDateProvided() {
        return isUntilDateProvided;
    }

    public boolean isLastModifiedDateIncluded() {
        return isLastModifiedDateIncluded;
    }

    public boolean isShallowCloningPerformed() {
        return isShallowCloningPerformed;
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

    public boolean isFileSizeLimitIgnored() {
        return isFileSizeLimitIgnored;
    }

    public int getNumCloningThreads() {
        return numCloningThreads;
    }

    public int getNumAnalysisThreads() {
        return numAnalysisThreads;
    }

    public boolean isFindingPreviousAuthorsPerformed() {
        return isFindingPreviousAuthorsPerformed;
    }

    public boolean isTestMode() {
        return isTestMode;
    }

    public boolean isFreshClonePerformed() {
        return isFreshClonePerformed;
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
                && this.isLastModifiedDateIncluded == otherCliArguments.isLastModifiedDateIncluded
                && this.isShallowCloningPerformed == otherCliArguments.isShallowCloningPerformed
                && this.isAutomaticallyLaunching == otherCliArguments.isAutomaticallyLaunching
                && this.isStandaloneConfigIgnored == otherCliArguments.isStandaloneConfigIgnored
                && this.numCloningThreads == otherCliArguments.numCloningThreads
                && this.numAnalysisThreads == otherCliArguments.numAnalysisThreads
                && this.zoneId.equals(otherCliArguments.zoneId)
                && this.isFindingPreviousAuthorsPerformed == otherCliArguments.isFindingPreviousAuthorsPerformed
                && this.isFileSizeLimitIgnored == otherCliArguments.isFileSizeLimitIgnored
                && this.isTestMode == otherCliArguments.isTestMode
                && this.isFreshClonePerformed == otherCliArguments.isFreshClonePerformed;
    }
}
