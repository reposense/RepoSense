package data;

import java.util.ArrayList;

/**
 * Created by matanghao1 on 3/6/17.
 */
public class FileInfo {

    String path;
    ArrayList<Line> lines;

    public FileInfo(String path,ArrayList<Line> lines) {
        this.path = path;
        this.lines = lines;
    }

    public Line getLineByNumber(int num){
        return lines.get(num);
    }

    public ArrayList<Line> getLines() {
        return lines;
    }

    public void setLines(ArrayList<Line> lines) {
        this.lines = lines;
    }

    public String getPath() {

        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


}
