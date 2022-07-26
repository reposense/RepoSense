package reposense.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import reposense.parser.AuthorConfigCsvParser;
import reposense.parser.GroupConfigCsvParser;
import reposense.parser.RepoConfigCsvParser;
import reposense.parser.ReportConfigJsonParser;

/**
 * Represents command line arguments user supplied when running the program with mandatory field -config.
 */
public class ConfigCliArguments extends CliArguments {
    private static final Path EMPTY_PATH = Paths.get("");

    private Path configFolderPath;
    private Path repoConfigFilePath;
    private Path authorConfigFilePath;
    private Path groupConfigFilePath;
    private Path reportConfigFilePath;
    private ReportConfiguration reportConfiguration;

    private final boolean isTestMode;
    private final boolean isFreshClonePerformed;

    public ConfigCliArguments(Path configFolderPath, Path outputFilePath, Path assetsFilePath, LocalDateTime sinceDate,
            LocalDateTime untilDate, boolean isSinceDateProvided, boolean isUntilDateProvided, int numCloningThreads,
            int numAnalysisThreads, List<FileType> formats, boolean isLastModifiedDateIncluded,
            boolean isShallowCloningPerformed, boolean isAutomaticallyLaunching, boolean isStandaloneConfigIgnored,
            boolean isFileSizeLimitIgnored, ZoneId zoneId, ReportConfiguration reportConfiguration,
            boolean isFindingPreviousAuthorsPerformed, boolean isTestMode, boolean isFreshClonePerformed) {
        this.configFolderPath = configFolderPath.equals(EMPTY_PATH)
                ? configFolderPath.toAbsolutePath()
                : configFolderPath;
        this.repoConfigFilePath = configFolderPath.resolve(RepoConfigCsvParser.REPO_CONFIG_FILENAME);
        this.authorConfigFilePath = configFolderPath.resolve(AuthorConfigCsvParser.AUTHOR_CONFIG_FILENAME);
        this.groupConfigFilePath = configFolderPath.resolve(GroupConfigCsvParser.GROUP_CONFIG_FILENAME);
        this.reportConfigFilePath = configFolderPath.resolve(ReportConfigJsonParser.REPORT_CONFIG_FILENAME);
        this.outputFilePath = outputFilePath;
        this.assetsFilePath = assetsFilePath;
        this.sinceDate = sinceDate;
        this.untilDate = untilDate;
        this.isSinceDateProvided = isSinceDateProvided;
        this.isUntilDateProvided = isUntilDateProvided;
        this.formats = formats;
        this.isLastModifiedDateIncluded = isLastModifiedDateIncluded;
        this.isShallowCloningPerformed = isShallowCloningPerformed;
        this.isAutomaticallyLaunching = isAutomaticallyLaunching;
        this.isStandaloneConfigIgnored = isStandaloneConfigIgnored;
        this.isFileSizeLimitIgnored = isFileSizeLimitIgnored;
        this.numCloningThreads = numCloningThreads;
        this.numAnalysisThreads = numAnalysisThreads;
        this.zoneId = zoneId;
        this.reportConfiguration = reportConfiguration;
        this.isFindingPreviousAuthorsPerformed = isFindingPreviousAuthorsPerformed;
        this.isTestMode = isTestMode;
        this.isFreshClonePerformed = isFreshClonePerformed;
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
        if (!(other instanceof ConfigCliArguments)) {
            return false;
        }

        ConfigCliArguments otherConfigCliArguments = (ConfigCliArguments) other;

        return super.equals(other)
                && this.repoConfigFilePath.equals(otherConfigCliArguments.repoConfigFilePath)
                && this.authorConfigFilePath.equals(otherConfigCliArguments.authorConfigFilePath)
                && this.groupConfigFilePath.equals(otherConfigCliArguments.groupConfigFilePath)
                && this.reportConfigFilePath.equals(otherConfigCliArguments.reportConfigFilePath)
                && this.isTestMode == otherConfigCliArguments.isTestMode
                && this.isFreshClonePerformed == otherConfigCliArguments.isFreshClonePerformed;
    }
}
