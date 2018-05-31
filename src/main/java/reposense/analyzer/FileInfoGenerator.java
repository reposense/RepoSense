package reposense.analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import reposense.dataobject.FileInfo;
import reposense.dataobject.LineInfo;


public class FileInfoGenerator {
    public static FileInfo generateFileInfo(String repoRoot, String relativePath) {
        FileInfo result = new FileInfo(relativePath);
        Path path = Paths.get(repoRoot, relativePath);
        try (BufferedReader br = new BufferedReader(Files.newBufferedReader(path))) {
            String line;
            int lineNum = 1;
            while ((line = br.readLine()) != null) {
                result.getLines().add(new LineInfo(lineNum, line));
                lineNum += 1;
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return result;
    }
}
