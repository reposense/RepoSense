package reposense.model;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

import reposense.parser.ArgsParser;
import reposense.parser.AuthorConfigCsvParser;
import reposense.parser.GroupConfigCsvParser;
import reposense.parser.RepoConfigCsvParser;
import reposense.parser.ReportConfigJsonParser;

/**
 * Represents command line arguments user supplied when running the program.
 */
public class CliArguments {
    private static final Path EMPTY_PATH = Paths.get("");
    private static final Path DEFAULT_CONFIG_PATH = Paths.get(System.getProperty("user.dir")
            + File.separator + "config" + File.separator);

    private Path outputFilePath;
    private Path assetsFilePath;
    private LocalDateTime sinceDate;
    private LocalDateTime untilDate;
    private boolean isSinceDateProvided;
    private boolean isUntilDateProvided;
    private List<FileType> formats;
    private boolean isLastModifiedDateIncluded;
    private boolean isShallowCloningPerformed;
    private boolean isAutomaticallyLaunching;
    private boolean isStandaloneConfigIgnored;
    private boolean isFileSizeLimitIgnored;
    private int numCloningThreads;
    private int numAnalysisThreads;
    private ZoneId zoneId;
    private boolean isFindingPreviousAuthorsPerformed;
    private boolean isTestMode = ArgsParser.DEFAULT_IS_TEST_MODE;
    private boolean isFreshClonePerformed = ArgsParser.DEFAULT_SHOULD_FRESH_CLONE;


    // ViewCliArguments.java
    private List<String> locations;

    // LocationsCliArguments.java
    private Path reportDirectoryPath;

    // ConfigCliArguments.java
    private Path configFolderPath;
    private Path repoConfigFilePath;
    private Path authorConfigFilePath;
    private Path groupConfigFilePath;
    private Path reportConfigFilePath;
    private ReportConfiguration reportConfiguration;

    public CliArguments(Builder builder) {
        this.outputFilePath = builder.outputFilePath;
        this.assetsFilePath = builder.assetsFilePath;
        this.sinceDate = builder.sinceDate;
        this.untilDate = builder.untilDate;
        this.isSinceDateProvided = builder.isSinceDateProvided;
        this.isUntilDateProvided = builder.isUntilDateProvided;
        this.formats = builder.formats;
        this.isLastModifiedDateIncluded = builder.isLastModifiedDateIncluded;
        this.isShallowCloningPerformed = builder.isShallowCloningPerformed;
        this.isAutomaticallyLaunching = builder.isAutomaticallyLaunching;
        this.isStandaloneConfigIgnored = builder.isStandaloneConfigIgnored;
        this.isFileSizeLimitIgnored = builder.isFileSizeLimitIgnored;
        this.numCloningThreads = builder.numCloningThreads;
        this.numAnalysisThreads = builder.numAnalysisThreads;
        this.zoneId = builder.zoneId;
        this.isFindingPreviousAuthorsPerformed = builder.isFindingPreviousAuthorsPerformed;
        this.isTestMode = builder.isTestMode;
        this.isFreshClonePerformed = builder.isFreshClonePerformed;
        this.locations = builder.locations;
        this.reportDirectoryPath = builder.reportDirectoryPath;
        this.configFolderPath = builder.configFolderPath;
        this.repoConfigFilePath = builder.repoConfigFilePath;
        this.authorConfigFilePath = builder.authorConfigFilePath;
        this.groupConfigFilePath = builder.groupConfigFilePath;
        this.reportConfigFilePath = builder.reportConfigFilePath;
        this.reportConfiguration = builder.reportConfiguration;
    }

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

    public List<String> getLocations() {
        return locations;
    }

    public Path getReportDirectoryPath() {
        return reportDirectoryPath;
    }

    public Path getConfigFolderPath() {
        return configFolderPath;
    }

    public Path getRepoConfigFilePath() {
        return repoConfigFilePath;
    }

    public Path getAuthorConfigFilePath() {
        return authorConfigFilePath;
    }

    public Path getGroupConfigFilePath() {
        return groupConfigFilePath;
    }

    public Path getReportConfigFilePath() {
        return reportConfigFilePath;
    }

    public ReportConfiguration getReportConfiguration() {
        return reportConfiguration;
    }

    public boolean isViewMode() {
        return (reportDirectoryPath != null && !reportDirectoryPath.equals(EMPTY_PATH)
                    && configFolderPath.equals(DEFAULT_CONFIG_PATH) && locations == null);
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

        return Objects.equals(this.outputFilePath, otherCliArguments.outputFilePath)
                && Objects.equals(this.sinceDate, otherCliArguments.sinceDate)
                && Objects.equals(this.untilDate, otherCliArguments.untilDate)
                && this.isSinceDateProvided == otherCliArguments.isSinceDateProvided
                && this.isUntilDateProvided == otherCliArguments.isUntilDateProvided
                && Objects.equals(this.formats, otherCliArguments.formats)
                && this.isLastModifiedDateIncluded == otherCliArguments.isLastModifiedDateIncluded
                && this.isShallowCloningPerformed == otherCliArguments.isShallowCloningPerformed
                && this.isAutomaticallyLaunching == otherCliArguments.isAutomaticallyLaunching
                && this.isStandaloneConfigIgnored == otherCliArguments.isStandaloneConfigIgnored
                && this.numCloningThreads == otherCliArguments.numCloningThreads
                && this.numAnalysisThreads == otherCliArguments.numAnalysisThreads
                && Objects.equals(this.zoneId, otherCliArguments.zoneId)
                && this.isFindingPreviousAuthorsPerformed == otherCliArguments.isFindingPreviousAuthorsPerformed
                && this.isFileSizeLimitIgnored == otherCliArguments.isFileSizeLimitIgnored
                && this.isTestMode == otherCliArguments.isTestMode
                && this.isFreshClonePerformed == otherCliArguments.isFreshClonePerformed
                && Objects.equals(this.locations, otherCliArguments.locations)
                && Objects.equals(this.reportDirectoryPath, otherCliArguments.reportDirectoryPath)
                && Objects.equals(this.repoConfigFilePath, otherCliArguments.repoConfigFilePath)
                && Objects.equals(this.authorConfigFilePath, otherCliArguments.authorConfigFilePath)
                && Objects.equals(this.groupConfigFilePath, otherCliArguments.groupConfigFilePath)
                && Objects.equals(this.reportConfigFilePath, otherCliArguments.reportConfigFilePath);
    }

    /**
     * Builder used to build CliArguments
     */
    public static final class Builder {
        private Path outputFilePath;
        private Path assetsFilePath;
        private LocalDateTime sinceDate;
        private LocalDateTime untilDate;
        private boolean isSinceDateProvided;
        private boolean isUntilDateProvided;
        private List<FileType> formats;
        private boolean isLastModifiedDateIncluded;
        private boolean isShallowCloningPerformed;
        private boolean isAutomaticallyLaunching;
        private boolean isStandaloneConfigIgnored;
        private boolean isFileSizeLimitIgnored;
        private int numCloningThreads;
        private int numAnalysisThreads;
        private ZoneId zoneId;
        private boolean isFindingPreviousAuthorsPerformed;
        private boolean isTestMode;
        private boolean isFreshClonePerformed;
        private List<String> locations;
        private Path reportDirectoryPath;
        private Path configFolderPath;
        private Path repoConfigFilePath;
        private Path authorConfigFilePath;
        private Path groupConfigFilePath;
        private Path reportConfigFilePath;
        private ReportConfiguration reportConfiguration;

        public Builder() {
        }

        public Builder outputFilePath(Path outputFilePath) {
            this.outputFilePath = outputFilePath;
            return this;
        }

        public Builder assetsFilePath(Path assetsFilePath) {
            this.assetsFilePath = assetsFilePath;
            return this;
        }

        public Builder sinceDate(LocalDateTime sinceDate) {
            this.sinceDate = sinceDate;
            return this;
        }

        public Builder untilDate(LocalDateTime untilDate) {
            this.untilDate = untilDate;
            return this;
        }

        public Builder isSinceDateProvided(boolean isSinceDateProvided) {
            this.isSinceDateProvided = isSinceDateProvided;
            return this;
        }

        public Builder isUntilDateProvided(boolean isUntilDateProvided) {
            this.isUntilDateProvided = isUntilDateProvided;
            return this;
        }

        public Builder formats(List<FileType> formats) {
            this.formats = formats;
            return this;
        }

        public Builder isLastModifiedDateIncluded(boolean isLastModifiedDateIncluded) {
            this.isLastModifiedDateIncluded = isLastModifiedDateIncluded;
            return this;
        }

        public Builder isShallowCloningPerformed(boolean isShallowCloningPerformed) {
            this.isShallowCloningPerformed = isShallowCloningPerformed;
            return this;
        }

        public Builder isAutomaticallyLaunching(boolean isAutomaticallyLaunching) {
            this.isAutomaticallyLaunching = isAutomaticallyLaunching;
            return this;
        }

        public Builder isStandaloneConfigIgnored(boolean isStandaloneConfigIgnored) {
            this.isStandaloneConfigIgnored = isStandaloneConfigIgnored;
            return this;
        }

        public Builder isFileSizeLimitIgnored(boolean isFileSizeLimitIgnored) {
            this.isFileSizeLimitIgnored = isFileSizeLimitIgnored;
            return this;
        }

        public Builder numCloningThreads(int numCloningThreads) {
            this.numCloningThreads = numCloningThreads;
            return this;
        }

        public Builder numAnalysisThreads(int numAnalysisThreads) {
            this.numAnalysisThreads = numAnalysisThreads;
            return this;
        }

        public Builder zoneId(ZoneId zoneId) {
            this.zoneId = zoneId;
            return this;
        }

        public Builder isFindingPreviousAuthorsPerformed(boolean isFindingPreviousAuthorsPerformed) {
            this.isFindingPreviousAuthorsPerformed = isFindingPreviousAuthorsPerformed;
            return this;
        }

        public Builder isTestMode(boolean isTestMode) {
            this.isTestMode = isTestMode;
            return this;
        }

        public Builder isFreshClonePerformed(boolean isFreshClonePerformed) {
            this.isFreshClonePerformed = isFreshClonePerformed;
            return this;
        }

        public Builder locations(List<String> locations) {
            this.locations = locations;
            return this;
        }

        public Builder reportDirectoryPath(Path reportDirectoryPath) {
            this.reportDirectoryPath = reportDirectoryPath;
            return this;
        }

        public Builder configFolderPath(Path configFolderPath) {
            this.configFolderPath = configFolderPath.equals(EMPTY_PATH)
                ? configFolderPath.toAbsolutePath()
                : configFolderPath;;
            this.repoConfigFilePath = configFolderPath.resolve(RepoConfigCsvParser.REPO_CONFIG_FILENAME);
            this.authorConfigFilePath = configFolderPath.resolve(AuthorConfigCsvParser.AUTHOR_CONFIG_FILENAME);
            this.groupConfigFilePath = configFolderPath.resolve(GroupConfigCsvParser.GROUP_CONFIG_FILENAME);
            this.reportConfigFilePath = configFolderPath.resolve(ReportConfigJsonParser.REPORT_CONFIG_FILENAME);
            return this;
        }

        public Builder reportConfiguration(ReportConfiguration reportConfiguration) {
            this.reportConfiguration = reportConfiguration;
            return this;
        }

        public CliArguments build() {
            return new CliArguments(this);
        }
    }
}
