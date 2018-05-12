package reposense.analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import reposense.dataobject.FileInfo;
import reposense.dataobject.LineInfo;


public class FileInfoGenerator {
    public static FileInfo generateFileInfo(String repoRoot, String relativePath) {
        FileInfo result = new FileInfo(relativePath);
        File file = new File(repoRoot + '/' + relativePath);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNum = 1;
            while ((line = br.readLine()) != null) {
                result.getLines().add(new LineInfo(lineNum, line));
                lineNum += 1;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
