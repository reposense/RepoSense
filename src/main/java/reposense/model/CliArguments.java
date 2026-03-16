package reposense.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import reposense.model.reportconfig.ReportConfiguration;
import reposense.parser.ArgsParser;
import reposense.parser.AuthorConfigCsvParser;
import reposense.parser.GroupConfigCsvParser;
import reposense.parser.RepoConfigCsvParser;
import reposense.parser.ReportConfigYamlParser;

/**
 * Represents command line arguments user supplied when running the program.
 */
public class CliArguments {
    private static final Path EMPTY_PATH = Paths.get("");

    private Path outputFilePath;
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
    private boolean isPortfolio;
    private boolean isFreshClonePerformed = ArgsParser.DEFAULT_SHOULD_FRESH_CLONE;
    private boolean isOnlyTextRefreshed;
    private boolean isAuthorDedupMode;

    private List<String> locations;
    private boolean isViewModeOnly;

    private Path reportDirectoryPath;

    private Path configFolderPath;
    private Path repoConfigFilePath;
    private Path authorConfigFilePath;
    private Path groupConfigFilePath;
    private Path reportConfigFilePath;
    private ReportConfiguration reportConfiguration;
    private RepoBlurbMap repoBlurbMap;
    private AuthorBlurbMap authorBlurbMap;
    private ChartBlurbMap chartBlurbMap;

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

    public RepoBlurbMap getRepoBlurbMap() {
        return repoBlurbMap;
    }

    public AuthorBlurbMap getAuthorBlurbMap() {
        return authorBlurbMap;
    }

    public ChartBlurbMap getChartBlurbMap() {
        return chartBlurbMap;
    }

    /**
     * Merges the {@code blurbMap} from the blurbs file with the blurb map in {@code reportConfiguration}.
     *
     * @return the merged blurb map.
     */
    public RepoBlurbMap mergeWithReportConfigRepoBlurbMap() {
        if (reportConfiguration == null) {
            return repoBlurbMap;
        }
        RepoBlurbMap repoConfigRepoBlurbMap = reportConfiguration.getRepoBlurbMap();
        for (Map.Entry<String, String> entry : repoBlurbMap.getAllMappings().entrySet()) {
            repoConfigRepoBlurbMap.withRecord(entry.getKey(), entry.getValue());
        }
        return repoConfigRepoBlurbMap;
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

    public boolean areReportConfigRepositoriesConfigured() {
        return reportConfiguration != null && !reportConfiguration.getReportRepoConfigurations().isEmpty();
    }

    public boolean isPortfolio() {
        return isPortfolio;
    }

    public boolean isOnlyTextRefreshed() {
        return isOnlyTextRefreshed;
    }

    public boolean isAuthorDedupMode() {
        return isAuthorDedupMode;
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
                && this.isFreshClonePerformed == otherCliArguments.isFreshClonePerformed
                && Objects.equals(this.locations, otherCliArguments.locations)
                && this.isViewModeOnly == otherCliArguments.isViewModeOnly
                && Objects.equals(this.reportDirectoryPath, otherCliArguments.reportDirectoryPath)
                && Objects.equals(this.repoConfigFilePath, otherCliArguments.repoConfigFilePath)
                && Objects.equals(this.authorConfigFilePath, otherCliArguments.authorConfigFilePath)
                && Objects.equals(this.groupConfigFilePath, otherCliArguments.groupConfigFilePath)
                && Objects.equals(this.reportConfigFilePath, otherCliArguments.reportConfigFilePath)
                && Objects.equals(this.repoBlurbMap, otherCliArguments.repoBlurbMap)
                && Objects.equals(this.authorBlurbMap, otherCliArguments.authorBlurbMap)
                && Objects.equals(this.chartBlurbMap, otherCliArguments.chartBlurbMap)
                && this.isAuthorshipAnalyzed == otherCliArguments.isAuthorshipAnalyzed
                && Objects.equals(this.originalityThreshold, otherCliArguments.originalityThreshold)
                && this.isPortfolio == otherCliArguments.isPortfolio
                && this.isOnlyTextRefreshed == otherCliArguments.isOnlyTextRefreshed
                && this.isAuthorDedupMode == otherCliArguments.isAuthorDedupMode;
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
                    ReportConfigYamlParser.REPORT_CONFIG_FILENAME);
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
         * Adds the {@code repoBlurbMap} to CliArguments.
         */
        public Builder repoBlurbMap(RepoBlurbMap repoBlurbMap) {
            this.cliArguments.repoBlurbMap = repoBlurbMap;
            return this;
        }

        /**
         * Adds the {@code authorBlurbMap} to CliArguments.
         */
        public Builder authorBlurbMap(AuthorBlurbMap authorBlurbMap) {
            this.cliArguments.authorBlurbMap = authorBlurbMap;
            return this;
        }

        /**
         * Adds the {@code chartBlurbMap} to CliArguments.
         */
        public Builder chartBlurbMap(ChartBlurbMap chartBlurbMap) {
            this.cliArguments.chartBlurbMap = chartBlurbMap;
            return this;
        }

        /**
         * Adds the {@code isPortfolio} to CLIArguments.
         *
         * @param isPortfolio Is portfolio.
         */
        public Builder isPortfolio(boolean isPortfolio) {
            this.cliArguments.isPortfolio = isPortfolio;
            return this;
        }

        /**
         * Adds the {@code isTextRefreshedOnly} to CLIArguments.
         *
         * @param isOnlyTextRefreshed Is text refreshed only.
         */
        public Builder isOnlyTextRefreshed(boolean isOnlyTextRefreshed) {
            this.cliArguments.isOnlyTextRefreshed = isOnlyTextRefreshed;
            return this;
        }

        /**
         * Adds the {@code isAuthorDedupMode} to CLIArguments.
         *
         * @param isAuthorDedupMode Is author dedup mode.
         */
        public Builder isAuthorDedupMode(boolean isAuthorDedupMode) {
            this.cliArguments.isAuthorDedupMode = isAuthorDedupMode;
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
