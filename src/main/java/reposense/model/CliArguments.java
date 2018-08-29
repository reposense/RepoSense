package reposense.model;

import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Represents command line arguments user supplied when running the program.
 */
public abstract class CliArguments {
    protected Path configFolderPath;
    protected Path outputFilePath;
    protected Optional<Date> sinceDate;
    protected Optional<Date> untilDate;
    protected List<String> formats;

    public Path getConfigFolderPath() { return configFolderPath; }

    public Path getOutputFilePath() {
        return outputFilePath;
    }

    public Optional<Date> getSinceDate() {
        return sinceDate;
    }

    public Optional<Date> getUntilDate() {
        return untilDate;
    }

    public List<String> getFormats() {
        return formats;
    }
}
