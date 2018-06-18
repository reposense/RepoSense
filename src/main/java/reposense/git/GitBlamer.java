package reposense.git;

import reposense.dataobject.Author;
import reposense.dataobject.FileInfo;
import reposense.dataobject.RepoConfiguration;
import reposense.system.CommandRunner;


public class GitBlamer {

    private static final int AUTHOR_NAME_OFFSET = "author ".length();

    public static void aggregateBlameInfo(FileInfo fileInfo, RepoConfiguration config) {
        String raw = CommandRunner.blameRaw(
                config.getRepoRoot(), fileInfo.getPath(), config.getSinceDate(), config.getUntilDate());
        String[] rawLines = raw.split("\n");
        int lineCount = 0;
        for (String line : rawLines) {
            String authorRawName = line.substring(AUTHOR_NAME_OFFSET);
            Author author = config.getAuthorAliasMap().get(authorRawName);
            if (author == null) {
                author = new Author("-");
            }
            fileInfo.getLines().get(lineCount++).setAuthor(author);
        }
    }
}
