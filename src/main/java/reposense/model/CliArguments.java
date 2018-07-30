package reposense.model;

import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Represents command line arguments user supplied when running the program.
 */
public class CliArguments {
    private Path configFolderPath;
    private Path outputFilePath;
    private Optional<Date> sinceDate;
    private Optional<Date> untilDate;
    private List<String> formats;
    private Path reportDirectoryPath;

    public CliArguments(Path configFolderPath, Path outputFilePath, Optional<Date> sinceDate,
            Optional<Date> untilDate, List<String> formats, Path reportDirectoryPath) {
        this.configFolderPath = configFolderPath;
        this.outputFilePath = outputFilePath;
        this.sinceDate = sinceDate;
        this.untilDate = untilDate;
        this.formats = formats;
        this.reportDirectoryPath = reportDirectoryPath;
    }

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
}
