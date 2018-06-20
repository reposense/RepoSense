package reposense.report;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import reposense.analyzer.FileInfoAnalyzer;
import reposense.analyzer.FileInfoExtractor;
import reposense.analyzer.FileResultAggregator;
import reposense.dataobject.AuthorshipSummary;
import reposense.dataobject.FileInfo;
import reposense.dataobject.FileResult;
import reposense.dataobject.RepoConfiguration;

@SuppressWarnings("PMD")
/**
 * Class for generating of the authorship summary data for each repo.
 */
public class AuthorshipReporter {

    /**
     * Generates and returns the authorship summary for each repo in {@code config}.
     */
    public static AuthorshipSummary generateAuthorshipSummary(RepoConfiguration config) {
        List<FileInfo> fileInfos = FileInfoExtractor.extractFileInfos(config);

        List<FileResult> fileResults = fileInfos.stream()
                .map(fileInfo -> FileInfoAnalyzer.analyzeFile(config, fileInfo))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return FileResultAggregator.aggregateFileResult(fileResults, config.getAuthorList());
    }
}
