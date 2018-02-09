package analyzer;

import dataObject.FileInfo;
import dataObject.LineInfo;

import java.io.*;

/**
 * Created by matanghao1 on 8/2/18.
 */
public class FileInfoGenerator {
    public static FileInfo generateFileInfo(String repoRoot, String relativePath){
        FileInfo result = new FileInfo(relativePath);
        File file = new File(repoRoot+'/'+relativePath);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNum = 1;
            while ((line = br.readLine()) != null) {
                result.getLines().add(new LineInfo(lineNum,line));
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
