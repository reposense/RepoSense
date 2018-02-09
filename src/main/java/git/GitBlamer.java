package git;

import dataObject.Author;
import dataObject.FileInfo;
import dataObject.RepoConfiguration;
import system.CommandRunner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by matanghao1 on 3/6/17.
 */
public class GitBlamer {

    private static final Pattern INSERTION_PATTERN = Pattern.compile("^author (.+)");

    static public void aggregateBlameInfo(FileInfo fileInfo, RepoConfiguration config){
        //System.out.println("blaming " + fileInfo.getPath());
        String raw = CommandRunner.blameRaw(config.getRepoRoot(), fileInfo.getPath());
        String[] rawLines = raw.split("\n");
        int lineCount = 0;
        for (int i = 0; i < rawLines.length; i++) {
            Matcher m = INSERTION_PATTERN.matcher(rawLines[i]);
            if (m.find()){
                String authorRawName = m.group(1);
                Author author = config.getAuthorAliasMap().get(authorRawName);
                if (author == null){
                    author = new Author("-");
                }
                fileInfo.getLines().get(lineCount++).setAuthor(author);
            }
        }

    }
}
