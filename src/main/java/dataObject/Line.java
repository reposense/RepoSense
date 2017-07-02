package dataObject;

import java.util.ArrayList;

/**
 * Created by matanghao1 on 29/5/17.
 */
public class Line {
    public Line(int lineNumber,String authorName) {
        this.lineNumber = lineNumber;
        this.author = new Author(authorName);
        this.issues = new ArrayList<IssueInfo>();
    }

    private int lineNumber;
    private Author author;
    private ArrayList<IssueInfo> issues;


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

    public int getLineNumber() {

        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public boolean hasIssue(){
        return !issues.isEmpty();
    }
}
