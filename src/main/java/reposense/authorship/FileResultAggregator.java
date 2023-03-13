package reposense.authorship;

import java.util.List;

import reposense.authorship.model.AuthorshipSummary;
import reposense.authorship.model.FileResult;
import reposense.authorship.model.LineInfo;
import reposense.model.Author;
import reposense.model.FileType;

/**
 * Aggregates the file analysis results to get the contribution and issue summary for all authors.
 */
public class FileResultAggregator {

    /**
     * Returns the {@link AuthorshipSummary} generated from aggregating the {@code fileResults} and {@code fileTypes}
     * for each {@link Author} in the list of {@code authors}.
     */
    public AuthorshipSummary aggregateFileResult(List<FileResult> fileResults, List<Author> authors,
            List<FileType> fileTypes) {
        AuthorshipSummary authorContributionSummary = new AuthorshipSummary(fileResults, authors, fileTypes);
        for (FileResult fileResult : fileResults) {
            if (fileResult.isBinary()) {
                continue;
            }
            for (LineInfo lineInfo : fileResult.getLines()) {
                Author author = lineInfo.getAuthor();
                if (!authors.contains(author)) {
                    continue;
                }
                authorContributionSummary.addAuthorContributionCount(author, fileResult.getFileType());
            }
            if (fileResult.isIgnored()) {
                fileResult.clearLines();
            }
        }
        return authorContributionSummary;
    }
}
