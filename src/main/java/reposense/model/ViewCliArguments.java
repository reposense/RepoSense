package reposense.model;

import java.nio.file.Path;

/**
 * Represents command line arguments user supplied when running the program with mandatory field -view.
 */
public class ViewCliArguments extends CliArguments {
    private Path reportDirectoryPath;

    public ViewCliArguments(Path reportDirectoryPath) {
        this.reportDirectoryPath = reportDirectoryPath;
    }

    public Path getReportDirectoryPath() {
        return reportDirectoryPath;
    }
}
