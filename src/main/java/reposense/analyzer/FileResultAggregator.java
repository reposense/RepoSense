package reposense.analyzer;

import java.util.List;

import reposense.dataobject.Author;
import reposense.dataobject.AuthorContributionSummary;
import reposense.dataobject.FileResult;
import reposense.dataobject.LineInfo;

/**
 * Aggregates the file analysis results to get the contribution and issue summary for all authors.
 */
public class FileResultAggregator {

    /**
     * Returns the {@code AuthorContributionSummary} generated from aggregating the {@code fileResults}.
     */
    public static AuthorContributionSummary aggregateFileResult(List<FileResult> fileResults, List<Author> authors) {
        AuthorContributionSummary authorContributionSummary = new AuthorContributionSummary(fileResults, authors);
        for (FileResult fileResult : fileResults) {
            for (LineInfo lineInfo : fileResult.getLines()) {
                Author author = lineInfo.getAuthor();
                if (!authors.contains(author)) {
                    continue;
                }

                authorContributionSummary.addAuthorContributionCount(author);
            }
        }
        return authorContributionSummary;
    }
}
