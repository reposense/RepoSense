package analyzer;

import dataObject.FileInfo;
import dataObject.Line;
import system.CommandRunner;

import java.util.ArrayList;

/**
 * Created by matanghao1 on 3/6/17.
 */
public class BlameParser {
    static public void aggregateBlameInfo(FileInfo fileInfo, String repoRoot){
        String raw = CommandRunner.blameRaw(repoRoot, fileInfo.getPath());
        String[] rawLines = raw.split("\n");
        for (int i = 0; i < rawLines.length; i++) {
            String authorName = getAuthorNameFromSingleLine(rawLines[i]);
            fileInfo.getLines().get(i).setAuthorByName(authorName);
        }

    }
    static private String getAuthorNameFromSingleLine(String line) {
        return line.substring(line.indexOf(" ") + 1);
    }
}
