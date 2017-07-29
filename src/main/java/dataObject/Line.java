package dataObject;

import java.util.ArrayList;

/**
 * Created by matanghao1 on 29/5/17.
 */
public class Line {


    private int lineNumber;
    private Author author;
    private ArrayList<IssueInfo> issues;

    private MethodInfo methodInfo;


    private String content;

    public Line(int lineNumber, String content) {
        this.lineNumber = lineNumber;
        this.issues = new ArrayList<>();
        this.content = content;
    }

    public ArrayList<IssueInfo> getIssues() {
        return issues;
    }

    public void setIssues(ArrayList<IssueInfo> issues) {
        this.issues = issues;
    }


    public Author getAuthor() {
        return author;
    }



    public void setAuthor(Author author) {
        this.author = author;
    }

    public void setAuthorByName(String authorName) {
        this.author = new Author(authorName);
    }

    public int getLineNumber() {

        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean hasIssue(){
        return !issues.isEmpty();
    }

    public MethodInfo getMethodInfo() {
        return methodInfo;
    }

    public void setMethodInfo(MethodInfo methodInfo) {
        this.methodInfo = methodInfo;
    }
}

