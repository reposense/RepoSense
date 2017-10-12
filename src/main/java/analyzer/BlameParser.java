package analyzer;

import dataObject.FileInfo;
import system.CommandRunner;

/**
 * Created by matanghao1 on 3/6/17.
 */
public class BlameParser {
    static public void aggregateBlameInfo(FileInfo fileInfo, String repoRoot){
        //System.out.println("blaming " + fileInfo.getPath());
        String raw = CommandRunner.blameRaw(repoRoot, fileInfo.getPath());
        String[] rawLines = raw.split("\n");
        for (int i = 0; i < rawLines.length; i++) {
            String authorName = getAuthorNameFromSingleLine(rawLines[i]);
            fileInfo.getLines().get(i).setAuthorByName(authorName);
        }

    }
    static private String getAuthorNameFromSingleLine(String line) {
        return line.substring(line.indexOf("<") + 1,line.length()-1);
    }
}
