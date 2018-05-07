package dataObject;

import java.util.ArrayList;


public class LineInfo {

    private int lineNumber;
    private Author author;
    private ArrayList<IssueInfo> issues;

    private String content;

    public LineInfo(int lineNumber, String content) {
        this.lineNumber = lineNumber;
        //V this line is commented to reduce the size of the output JSON
        //this.issues = new ArrayList<>();
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

    public boolean hasIssue() {
        return !issues.isEmpty();
    }
}

