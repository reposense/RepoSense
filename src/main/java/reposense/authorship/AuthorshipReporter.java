package reposense.authorship;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import reposense.authorship.model.AuthorshipSummary;
import reposense.authorship.model.FileInfo;
import reposense.authorship.model.FileResult;
import reposense.model.RepoConfiguration;
import reposense.system.LogsManager;
import reposense.util.FileUtil;

/**
 * Generates the authorship summary data for each repository.
 */
public class AuthorshipReporter {
    private static final Logger logger = LogsManager.getLogger(AuthorshipReporter.class);

    private static final int HIGH_NUMBER_LINES_THRESHOLD = 500000;
    private static final String HIGH_NUMBER_LINES_MESSAGE = "There are a large number (%s) of text files to be "
            + "analyzed, comprising a total of %s lines. As such, RepoSense may take a long time to generate the "
            + "report. Performance may be improved by setting values for the \"File formats\" and "
            + " \"Ignore Glob List\" columns in the repo-config.csv so as to reduce the number of files to be "
            + " analyzed.";

    /**
     * Generates and returns the authorship summary for each repo in {@code config}.
     */
    public static AuthorshipSummary generateAuthorshipSummary(RepoConfiguration config) {
        List<FileInfo> textFileInfos = FileInfoExtractor.extractTextFileInfos(config);

        int numFiles = textFileInfos.size();
        int totalNumLines = textFileInfos.stream().mapToInt(fileInfo -> getNumLinesInFile(config, fileInfo)).sum();

        if (totalNumLines > HIGH_NUMBER_LINES_THRESHOLD) {
            logger.warning(String.format(HIGH_NUMBER_LINES_MESSAGE, numFiles, totalNumLines));
        }

        List<FileResult> fileResults = textFileInfos.stream()
                .map(fileInfo -> FileInfoAnalyzer.analyzeTextFile(config, fileInfo))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<FileInfo> binaryFileInfos = FileInfoExtractor.extractBinaryFileInfos(config);

        List<FileResult> binaryFileResults = binaryFileInfos.stream()
                .map(fileInfo -> FileInfoAnalyzer.analyzeBinaryFile(config, fileInfo))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        fileResults.addAll(binaryFileResults);

        return FileResultAggregator.aggregateFileResult(fileResults, config.getAuthorList(),
                config.getAllFileTypes());
    }

    /**
     * Returns the number of lines in the file represented by {@code textFileInfo}.
     */
    private static int getNumLinesInFile(RepoConfiguration config, FileInfo textFileInfo) {
        BufferedReader reader;
        String path = Paths.get(FileUtil.getRepoParentFolder(config).toString(), config.getRepoName(),
                textFileInfo.getPath().replace("/", "\\")).toString();
        try {
            reader = new BufferedReader(new FileReader(path));
        } catch (FileNotFoundException fnfe) {
            return 0;
        }
        int lines = 0;
        try {
            while (reader.readLine() != null) {
                lines++;
            }
            reader.close();
        } catch (IOException ioe) {
            return lines;
        }
        return lines;
    }
}
