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
    protected Path reportDirectoryPath;
    protected List<String> locations;

    public CliArguments(
            Path outputFilePath, Optional<Date> sinceDate, Optional<Date> untilDate, List<String> formats) {
        this.outputFilePath = outputFilePath;
        this.sinceDate = sinceDate;
        this.untilDate = untilDate;
        this.formats = formats;
    }

    public CliArguments() {}

    public Path getConfigFolderPath() {
        return configFolderPath;
    }

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

    public Path getReportDirectoryPath() {
        return reportDirectoryPath;
    }

    public List<String> getLocations() {
        return locations;
    }
}
