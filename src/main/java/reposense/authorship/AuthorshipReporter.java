package reposense.authorship;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import reposense.authorship.model.AuthorshipSummary;
import reposense.authorship.model.FileInfo;
import reposense.authorship.model.FileResult;
import reposense.model.RepoConfiguration;

/**
 * Generates the authorship summary data for each repository.
 */
public class AuthorshipReporter {

    /**
     * Generates and returns the authorship summary for each repo in {@code config}.
     */
    public static AuthorshipSummary generateAuthorshipSummary(RepoConfiguration config) {
        List<FileInfo> textFileInfos = FileInfoExtractor.extractTextFileInfos(config);

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
