package reposense.model;

import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import reposense.parser.AuthorConfigCsvParser;
import reposense.parser.RepoConfigCsvParser;

/**
 * Represents command line arguments user supplied when running the program with mandatory field -config.
 */
public class ConfigCliArguments extends CliArguments {
    private Path configFolderPath;
    private Path repoConfigFilePath;
    private Path authorConfigFilePath;

    public ConfigCliArguments(Path configFolderPath,
            Path outputFilePath, Optional<Date> sinceDate, Optional<Date> untilDate, List<Format> formats) {
        this.configFolderPath = configFolderPath;
        this.repoConfigFilePath = configFolderPath.resolve(RepoConfigCsvParser.REPO_CONFIG_FILENAME);
        this.authorConfigFilePath = configFolderPath.resolve(AuthorConfigCsvParser.AUTHOR_CONFIG_FILENAME);
        this.outputFilePath = outputFilePath;
        this.sinceDate = sinceDate;
        this.untilDate = untilDate;
        this.formats = formats;
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
