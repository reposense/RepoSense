package dataObject;

/**
 * Created by matanghao1 on 19/6/17.
 */
public class MethodInfo {
    private int start;
    private int end;
    private String methodName;
    private Author owner;

    public MethodInfo(int start, int end, String methodName) {
        this.start = start;
        this.end = end;
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public Author getOwner() {
        return owner;
    }

    public void setOwner(Author owner) {
        this.owner = owner;
    }

    public int getTotalLines(){
        return end - start + 1;
    }
}
