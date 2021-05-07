package reposense.authorship;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import reposense.authorship.model.AuthorshipSummary;
import reposense.authorship.model.FileInfo;
import reposense.authorship.model.FileResult;
import reposense.model.RepoConfiguration;
import reposense.system.LogsManager;


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
        int totalNumLines = textFileInfos.stream()
                .mapToInt(fileInfo -> fileInfo.getNumOfLines())
                .sum();

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
}
