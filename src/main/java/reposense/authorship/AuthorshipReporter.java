package reposense.authorship;

import java.time.ZoneId;
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
     * Generates and returns the authorship summary for each repo in {@code config}, where the time is given in
     * {@code zoneId} timezone.
     */
    public static AuthorshipSummary generateAuthorshipSummary(RepoConfiguration config, ZoneId zoneId) {
        List<FileInfo> fileInfos = FileInfoExtractor.extractFileInfos(config);

        List<FileResult> fileResults = fileInfos.stream()
                .map(fileInfo -> FileInfoAnalyzer.analyzeFile(config, fileInfo, zoneId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return FileResultAggregator.aggregateFileResult(fileResults, config.getAuthorList(),
                config.getAllFileTypes());
    }
}
