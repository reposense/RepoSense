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
     * Generates and returns the authorship summary for the repositories in {@code config} by analysising all relevant
     * files according to {@code config}.
     */
    public static AuthorshipSummary generateAuthorshipSummary(RepoConfiguration config) {
        List<FileInfo> fileInfos = FileInfoExtractor.extractFileInfos(config);

        List<FileResult> fileResults = fileInfos.stream()
                .map(fileInfo -> FileInfoAnalyzer.analyzeFile(config, fileInfo))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return FileResultAggregator.aggregateFileResult(fileResults, config.getAuthorList());
    }

    /**
     * Generates and returns the authorship summary for the repositories in {@code config}, skipping analysis on files
     * unchanged based on {@code fileResults} generated from a previous analysis.
     */
    public static AuthorshipSummary updateAuthorshipSummary(RepoConfiguration config, List<FileResult> fileResults) {
        List<FileInfo> oldFileInfos = fileResults.stream()
                .map(fileResult -> new FileInfo(fileResult.getPath(), fileResult.getLines()))
                .collect(Collectors.toList());

        List<FileInfo> updatedFileInfos = FileInfoExtractor.updateFileInfos(config, oldFileInfos);

        List<FileResult> updatedFileResults = updatedFileInfos.stream()
                .map(fileInfo -> FileInfoAnalyzer.analyzeFile(config, fileInfo))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return FileResultAggregator.aggregateFileResult(updatedFileResults, config.getAuthorList());
    }
}
