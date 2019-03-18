package reposense.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import reposense.parser.AuthorConfigCsvParser;
import reposense.parser.RepoConfigCsvParser;

/**
 * Represents command line arguments user supplied when running the program with mandatory field -config.
 */
public class ConfigCliArguments extends CliArguments {
    private static final Path EMPTY_PATH = Paths.get("");

    private Path configFolderPath;
    private Path repoConfigFilePath;
    private Path authorConfigFilePath;

    public ConfigCliArguments(Path configFolderPath, Path outputFilePath, Optional<Date> sinceDate,
            Optional<Date> untilDate, List<Format> formats, boolean isAutomaticallyLaunching) {
        this.configFolderPath = configFolderPath.equals(EMPTY_PATH)
                ? configFolderPath.toAbsolutePath()
                : configFolderPath;
        this.repoConfigFilePath = configFolderPath.resolve(RepoConfigCsvParser.REPO_CONFIG_FILENAME);
        this.authorConfigFilePath = configFolderPath.resolve(AuthorConfigCsvParser.AUTHOR_CONFIG_FILENAME);
        this.outputFilePath = outputFilePath;
        this.sinceDate = sinceDate;
        this.untilDate = untilDate;
        this.formats = formats;
        this.isAutomaticallyLaunching = isAutomaticallyLaunching;
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
                && this.authorConfigFilePath.equals(otherConfigCliArguments.authorConfigFilePath);
    }
}
