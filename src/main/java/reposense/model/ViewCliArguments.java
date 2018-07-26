package reposense.model;

import java.nio.file.Path;

public class ViewCliArguments extends CliArguments {
    public ViewCliArguments(Path reportDirectoryPath) {
        this.reportDirectoryPath = reportDirectoryPath;
    }
}
