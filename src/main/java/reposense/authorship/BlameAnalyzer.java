package reposense.authorship;

import java.util.Map;

import reposense.dataobject.Author;
import reposense.dataobject.RepoConfiguration;
import reposense.system.CommandRunner;

/**
 * Analyzes the authorship of a {@code FileInfo} using the git blame result.
 */
public class BlameAnalyzer {
    private static final int AUTHOR_NAME_OFFSET = "author ".length();

    /**
     * Sets the {@code Author} for each line in {@code fileInfo} based on the git blame analysis on the file.
     */
    public static void aggregateBlameAuthorInfo(RepoConfiguration config, FileInfo fileInfo) {
        Map<String, Author> authorAliasMap = config.getAuthorAliasMap();

        String blameResults = getGitBlameResult(config, fileInfo.getPath());
        String[] blameResultLines = blameResults.split("\n");
        int lineCount = 0;

        for (String line : blameResultLines) {
            String authorRawName = line.substring(AUTHOR_NAME_OFFSET);
            Author author = authorAliasMap.getOrDefault(authorRawName, new Author(Author.UNKNOWN_AUTHOR_GIT_ID));
            fileInfo.setLineAuthor(lineCount++, author);
        }
    }

    /**
     * Returns the analysis result from running git blame on {@code filePath}.
     */
    private static String getGitBlameResult(RepoConfiguration config, String filePath) {
        return CommandRunner.blameRaw(config.getRepoRoot(), filePath, config.getSinceDate(), config.getUntilDate());
    }
}
