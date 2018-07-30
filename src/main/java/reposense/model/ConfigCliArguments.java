package reposense.model;

import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class ConfigCliArguments extends CliArguments {
    public ConfigCliArguments(Path configFolderPath,
            Path outputFilePath, Optional<Date> sinceDate, Optional<Date> untilDate, List<String> formats) {
        super(outputFilePath, sinceDate, untilDate, formats);
        this.configFolderPath = configFolderPath;
    }
}
