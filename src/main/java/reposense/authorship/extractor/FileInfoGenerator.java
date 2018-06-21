package reposense.authorship.extractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import reposense.authorship.model.FileInfo;
import reposense.authorship.model.LineInfo;
import reposense.system.LogsManager;

public class FileInfoGenerator {
    private static final Logger logger = LogsManager.getLogger(FileInfoGenerator.class);

    /**
     * Generates and returns a {@code FileInfo} with a list of {@code LineInfo} for each line content in the
     * {@code relativePath} file.
     */
    public static FileInfo generateFileInfo(String repoRoot, String relativePath) {
        FileInfo fileInfo = new FileInfo(relativePath);
        Path path = Paths.get(repoRoot, relativePath);
        try (BufferedReader br = new BufferedReader(Files.newBufferedReader(path))) {
            String line;
            int lineNum = 1;
            while ((line = br.readLine()) != null) {
                fileInfo.addLine(new LineInfo(lineNum++, line));
            }
        } catch (IOException ioe) {
            logger.log(Level.SEVERE, ioe.getMessage(), ioe);
        }
        return fileInfo;
    }
}
