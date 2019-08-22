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
        List<FileInfo> fileInfos = FileInfoExtractor.extractFileInfos(config);

        List<FileResult> fileResults = fileInfos.stream()
                .map(fileInfo -> FileInfoAnalyzer.analyzeFile(config, fileInfo))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return FileResultAggregator.aggregateFileResult(fileResults, config.getAuthorList(),
                config.getAllFileTypes());
    }
}
