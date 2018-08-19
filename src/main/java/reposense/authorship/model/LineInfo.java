package reposense.authorship.model;

import java.util.ArrayList;
import java.util.Objects;

import reposense.model.Author;

/**
 * Stores the information of a line in a {@code FileInfo}.
 */
public class LineInfo {
    private int lineNumber;
    private Author author;
    private String content;
    private ArrayList<IssueInfo> issues;

    private transient boolean isTracked;

    public LineInfo(int lineNumber, String content) {
        this.lineNumber = lineNumber;
        //V this line is commented to reduce the size of the output JSON
        //this.issues = new ArrayList<>();
        this.content = content;

        isTracked = true;
    }

    public ArrayList<IssueInfo> getIssues() {
        return issues;
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

    public String getContent() {
        return content;
    }

    public void setTracked(boolean isTracked) {
        this.isTracked = isTracked;
    }

    public boolean isTracked() {
        return isTracked;
    }


    public boolean hasIssue() {
        return !issues.isEmpty();
    }

    public void addNewIssue(IssueInfo issueInfo) {
        issues.add(issueInfo);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof LineInfo)) {
            return false;
        }

        LineInfo otherLineInfo = (LineInfo) other;
        return lineNumber == otherLineInfo.lineNumber
                && Objects.equals(author, otherLineInfo.author)
                && content.equals(otherLineInfo.content)
                && Objects.equals(issues, otherLineInfo.issues)
                && isTracked == otherLineInfo.isTracked;
    }
}

