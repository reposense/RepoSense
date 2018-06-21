package reposense.authorship.model;

import reposense.model.Author;

public class MethodInfo {

    private static int METHOD_IDENTIFIER_COUNT = 0;

    private int start;
    private int end;
    private String methodName;
    private Author owner;
    private int methodUid;

    public MethodInfo(int start, int end, String methodName) {
        this.start = start;
        this.end = end;
        this.methodName = methodName;
        this.methodUid = ++METHOD_IDENTIFIER_COUNT;
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

    public int getTotalLines() {
        return end - start + 1;
    }

    public int getMethodUid() {
        return methodUid;
    }

    public void setMethodUid(int methodUid) {
        this.methodUid = methodUid;
    }
}
