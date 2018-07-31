package reposense.model;

import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Represents command line arguments user supplied when running the program with mandatory field -config.
 */
public class ConfigCliArguments extends CliArguments {
    private Path configFolderPath;

    public ConfigCliArguments(Path configFolderPath,
            Path outputFilePath, Optional<Date> sinceDate, Optional<Date> untilDate, List<String> formats) {
        this.configFolderPath = configFolderPath;
        this.outputFilePath = outputFilePath;
        this.sinceDate = sinceDate;
        this.untilDate = untilDate;
        this.formats = formats;
    }

    public Path getConfigFolderPath() {
        return configFolderPath;
    }
}
