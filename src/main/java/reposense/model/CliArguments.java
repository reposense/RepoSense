package reposense.model;

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
    private boolean isAuthorshipAnalyzed;
    private double originalityThreshold;
    private boolean isTestMode = ArgsParser.DEFAULT_IS_TEST_MODE;
    private boolean isFreshClonePerformed = ArgsParser.DEFAULT_SHOULD_FRESH_CLONE;

    private List<String> locations;
    private boolean isViewModeOnly;

    private Path reportDirectoryPath;

    private Path configFolderPath;
    private Path repoConfigFilePath;
    private Path authorConfigFilePath;
    private Path groupConfigFilePath;
    private Path reportConfigFilePath;
    private ReportConfiguration reportConfiguration;

    /**
     * Constructs a {@code CliArguments} object without any parameters.
     */
    private CliArguments() {}

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

    public boolean isViewModeOnly() {
        return isViewModeOnly;
    }

    public boolean isAuthorshipAnalyzed() {
        return isAuthorshipAnalyzed;
    }

    public double getOriginalityThreshold() {
        return originalityThreshold;
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
                && this.isViewModeOnly == otherCliArguments.isViewModeOnly
                && Objects.equals(this.reportDirectoryPath, otherCliArguments.reportDirectoryPath)
                && Objects.equals(this.repoConfigFilePath, otherCliArguments.repoConfigFilePath)
                && Objects.equals(this.authorConfigFilePath, otherCliArguments.authorConfigFilePath)
                && Objects.equals(this.groupConfigFilePath, otherCliArguments.groupConfigFilePath)
                && Objects.equals(this.reportConfigFilePath, otherCliArguments.reportConfigFilePath)
                && this.isAuthorshipAnalyzed == otherCliArguments.isAuthorshipAnalyzed
                && Objects.equals(this.originalityThreshold, otherCliArguments.originalityThreshold);
    }

    /**
     * Builder used to build CliArguments.
     */
    public static final class Builder {
        private CliArguments cliArguments;

        public Builder() {
            this.cliArguments = new CliArguments();
        }

        /**
         * Adds the {@code outputFilePath} to CliArguments.
         *
         * @param outputFilePath The output file path.
         */
        public Builder outputFilePath(Path outputFilePath) {
            this.cliArguments.outputFilePath = outputFilePath;
            return this;
        }

        /**
         * Adds the {@code assetsFilePath} to CliArguments.
         *
         * @param assetsFilePath The assets file path.
         */
        public Builder assetsFilePath(Path assetsFilePath) {
            this.cliArguments.assetsFilePath = assetsFilePath;
            return this;
        }

        /**
         * Adds the {@code sinceDate} to CliArguments.
         *
         * @param sinceDate The since date.
         */
        public Builder sinceDate(LocalDateTime sinceDate) {
            this.cliArguments.sinceDate = sinceDate;
            return this;
        }

        /**
         * Adds the {@code untilDate} to CliArguments.
         *
         * @param untilDate The until date.
         */
        public Builder untilDate(LocalDateTime untilDate) {
            this.cliArguments.untilDate = untilDate;
            return this;
        }

        /**
         * Adds the {@code isSinceDateProvided} to CliArguments.
         *
         * @param isSinceDateProvided Is the since date provided.
         */
        public Builder isSinceDateProvided(boolean isSinceDateProvided) {
            this.cliArguments.isSinceDateProvided = isSinceDateProvided;
            return this;
        }

        /**
         * Adds the {@code isUntilDateProvided} to CliArguments.
         *
         * @param isUntilDateProvided Is the until date provided.
         */
        public Builder isUntilDateProvided(boolean isUntilDateProvided) {
            this.cliArguments.isUntilDateProvided = isUntilDateProvided;
            return this;
        }

        /**
         * Adds the {@code formats} to CliArguments.
         *
         * @param formats The list of {@link FileType}.
         */
        public Builder formats(List<FileType> formats) {
            this.cliArguments.formats = formats;
            return this;
        }

        /**
         * Adds the {@code isLastModifiedDateIncluded} to CliArguments.
         *
         * @param isLastModifiedDateIncluded Is the last modified date included.
         */
        public Builder isLastModifiedDateIncluded(boolean isLastModifiedDateIncluded) {
            this.cliArguments.isLastModifiedDateIncluded = isLastModifiedDateIncluded;
            return this;
        }

        /**
         * Adds the {@code isShallowCloningPerformed} to CliArguments.
         *
         * @param isShallowCloningPerformed Is shallow cloning performed.
         */
        public Builder isShallowCloningPerformed(boolean isShallowCloningPerformed) {
            this.cliArguments.isShallowCloningPerformed = isShallowCloningPerformed;
            return this;
        }

        /**
         * Adds the {@code isAutomaticallyLaunching} to CliArguments.
         *
         * @param isAutomaticallyLaunching Is automatically launching.
         */
        public Builder isAutomaticallyLaunching(boolean isAutomaticallyLaunching) {
            this.cliArguments.isAutomaticallyLaunching = isAutomaticallyLaunching;
            return this;
        }

        /**
         * Adds the {@code isStandaloneConfigIgnored} to CliArguments.
         *
         * @param isStandaloneConfigIgnored Is standalone config ignored.
         */
        public Builder isStandaloneConfigIgnored(boolean isStandaloneConfigIgnored) {
            this.cliArguments.isStandaloneConfigIgnored = isStandaloneConfigIgnored;
            return this;
        }

        /**
         * Adds the {@code isFileSizeLimitIgnored} to CliArguments.
         *
         * @param isFileSizeLimitIgnored Is file size limit ignored.
         */
        public Builder isFileSizeLimitIgnored(boolean isFileSizeLimitIgnored) {
            this.cliArguments.isFileSizeLimitIgnored = isFileSizeLimitIgnored;
            return this;
        }

        /**
         * Adds the {@code numCloningThreads} to CliArguments.
         *
         * @param numCloningThreads The number of cloning threads.
         */
        public Builder numCloningThreads(int numCloningThreads) {
            this.cliArguments.numCloningThreads = numCloningThreads;
            return this;
        }

        /**
         * Adds the {@code numAnalysisThreads} to CliArguments.
         *
         * @param numAnalysisThreads The number of analysis threads.
         */
        public Builder numAnalysisThreads(int numAnalysisThreads) {
            this.cliArguments.numAnalysisThreads = numAnalysisThreads;
            return this;
        }

        /**
         * Adds the {@code zoneId} to CliArguments.
         *
         * @param zoneId The timezone Id.
         */
        public Builder zoneId(ZoneId zoneId) {
            this.cliArguments.zoneId = zoneId;
            return this;
        }

        /**
         * Adds the {@code isFindingPreviousAuthorsPerformed} to CliArguments.
         *
         * @param isFindingPreviousAuthorsPerformed Is finding previous authors performed.
         */
        public Builder isFindingPreviousAuthorsPerformed(boolean isFindingPreviousAuthorsPerformed) {
            this.cliArguments.isFindingPreviousAuthorsPerformed = isFindingPreviousAuthorsPerformed;
            return this;
        }

        /**
         * Adds the {@code isTestMode} to CliArguments.
         *
         * @param isTestMode Is test mode.
         */
        public Builder isTestMode(boolean isTestMode) {
            this.cliArguments.isTestMode = isTestMode;
            return this;
        }

        /**
         * Adds the {@code isFreshClonePerformed} to CliArguments.
         *
         * @param isFreshClonePerformed Is fresh clone performed.
         */
        public Builder isFreshClonePerformed(boolean isFreshClonePerformed) {
            this.cliArguments.isFreshClonePerformed = isFreshClonePerformed;
            return this;
        }

        /**
         * Adds the {@code locations} to CliArguments.
         *
         * @param locations The list of locations.
         */
        public Builder locations(List<String> locations) {
            this.cliArguments.locations = locations;
            return this;
        }

        /**
         * Adds the {@code isViewModeOnly} to CliArguments.
         *
         * @param isViewModeOnly Is view mode only.
         */
        public Builder isViewModeOnly(boolean isViewModeOnly) {
            this.cliArguments.isViewModeOnly = isViewModeOnly;
            return this;
        }

        /**
         * Adds the {@code reportDirectoryPath} to CliArguments.
         *
         * @param reportDirectoryPath The report directory path.
         */
        public Builder reportDirectoryPath(Path reportDirectoryPath) {
            this.cliArguments.reportDirectoryPath = reportDirectoryPath;
            return this;
        }

        /**
         * Adds the {@code configFolderPath} to CliArguments. {@code configFolderPath} is utilised to configure the
         * {@code repoConfigFilePath}, {@code authorConfigFilePath}, {@code groupConfigFilePath} and
         * {@code reportConfigFilePath}.
         *
         * @param configFolderPath The config folder path.
         */
        public Builder configFolderPath(Path configFolderPath) {
            this.cliArguments.configFolderPath = configFolderPath.equals(EMPTY_PATH)
                    ? configFolderPath.toAbsolutePath()
                    : configFolderPath;
            this.cliArguments.repoConfigFilePath = configFolderPath.resolve(
                    RepoConfigCsvParser.REPO_CONFIG_FILENAME);
            this.cliArguments.authorConfigFilePath = configFolderPath.resolve(
                    AuthorConfigCsvParser.AUTHOR_CONFIG_FILENAME);
            this.cliArguments.groupConfigFilePath = configFolderPath.resolve(
                    GroupConfigCsvParser.GROUP_CONFIG_FILENAME);
            this.cliArguments.reportConfigFilePath = configFolderPath.resolve(
                    ReportConfigJsonParser.REPORT_CONFIG_FILENAME);
            return this;
        }

        /**
         * Adds the {@code reportConfiguration} to CliArguments.
         *
         * @param reportConfiguration The report configuration.
         */
        public Builder reportConfiguration(ReportConfiguration reportConfiguration) {
            this.cliArguments.reportConfiguration = reportConfiguration;
            return this;
        }

        /**
         * Adds the {@code isAuthorshipAnalyzed} to CliArguments.
         *
         * @param isAuthorshipAnalyzed Is authorship analyzed.
         */
        public Builder isAuthorshipAnalyzed(boolean isAuthorshipAnalyzed) {
            this.cliArguments.isAuthorshipAnalyzed = isAuthorshipAnalyzed;
            return this;
        }

        /**
         * Adds the {@code originalityThreshold} to CliArguments.
         *
         * @param originalityThreshold the originality threshold.
         */
        public Builder originalityThreshold(double originalityThreshold) {
            this.cliArguments.originalityThreshold = originalityThreshold;
            return this;
        }

        /**
         * Builds CliArguments.
         *
         * @return CliArguments
         */
        public CliArguments build() {
            CliArguments built = this.cliArguments;
            this.cliArguments = new CliArguments();
            return built;
        }
    }
}
