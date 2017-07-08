package dataObject;

import java.util.ArrayList;

/**
 * Created by matanghao1 on 3/6/17.
 */
public class FileInfo {

    String path;
    ArrayList<Line> lines;

    ArrayList<MethodInfo> methodInfos;

    public FileInfo(String path) {
        this.path = path;
        this.lines = new ArrayList<>();
    }

    public FileInfo(String path,ArrayList<Line> lines) {
        this.path = path;
        this.lines = lines;
    }

    public Line getLineByNumber(int num){
        return lines.get(num - 1);
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

    public ArrayList<MethodInfo> getMethodInfos() {
        return methodInfos;
    }

    public void setMethodInfos(ArrayList<MethodInfo> methodInfos) {
        this.methodInfos = methodInfos;
    }

}
