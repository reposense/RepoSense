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

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (this == other) {
            return true;
        }

        // instanceof handles null
        if (!(other instanceof ViewCliArguments)) {
            return false;
        }

        ViewCliArguments otherViewCliArguments = (ViewCliArguments) other;

        return super.equals(other)
                && this.reportDirectoryPath.equals(otherViewCliArguments.reportDirectoryPath);
    }
}
