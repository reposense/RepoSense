package reposense.git;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import reposense.dataObject.Author;
import reposense.dataObject.FileInfo;
import reposense.dataObject.RepoConfiguration;
import reposense.system.CommandRunner;


public class GitBlamer {

    private static final Pattern INSERTION_PATTERN = Pattern.compile("^author (.+)");

    public static void aggregateBlameInfo(FileInfo fileInfo, RepoConfiguration config) {
        //System.out.println("blaming " + fileInfo.getPath());
        String raw = CommandRunner.blameRaw(config.getRepoRoot(), fileInfo.getPath());
        String[] rawLines = raw.split("\n");
        int lineCount = 0;
        for (int i = 0; i < rawLines.length; i++) {
            Matcher m = INSERTION_PATTERN.matcher(rawLines[i]);
            if (m.find()) {
                String authorRawName = m.group(1);
                Author author = config.getAuthorAliasMap().get(authorRawName);
                if (author == null) {
                    author = new Author("-");
                }
                fileInfo.getLines().get(lineCount++).setAuthor(author);
            }
        }

    }
}
