package analyzer;

import dataObject.FileInfo;
import dataObject.Line;
import system.CommandRunner;

import java.util.ArrayList;

/**
 * Created by matanghao1 on 3/6/17.
 */
public class BlameParser {
    static public FileInfo blameSingleFile(String repoRoot, String relativePath){

        ArrayList<Line> result = new ArrayList<Line>();
        String raw = CommandRunner.blameRaw(repoRoot, relativePath);
        if (raw.contains("\n")) {
            String[] rawLines = raw.split("\n");
            for (int i = 0; i < rawLines.length; i++) {
                String authorName = getAuthorNameFromSingleLine(rawLines[i]);
                result.add(new Line(i, authorName));
            }
        }
        return new FileInfo(relativePath,result);
    }
    static private String getAuthorNameFromSingleLine(String line) {
        return line.substring(line.indexOf(" ") + 1);
    }
}
