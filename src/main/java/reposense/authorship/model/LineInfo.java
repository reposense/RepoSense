package reposense.authorship.model;

import java.util.ArrayList;

import reposense.model.Author;

/**
 * Stores the information of a line in a {@code FileInfo}.
 */
public class LineInfo {
    private int lineNumber;
    private Author author;
    private String content;
    private ArrayList<IssueInfo> issues;

    public LineInfo(int lineNumber, String content) {
        this.lineNumber = lineNumber;
        // this line is commented to reduce the size of the output JSON
        //this.issues = new ArrayList<>();
        this.content = content;
        author = new Author(Author.UNSET_AUTHOR_GIT_ID);
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

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public boolean hasIssue() {
        return !issues.isEmpty();
    }

    public void addNewIssue(IssueInfo issueInfo) {
        issues.add(issueInfo);
    }

    @Override
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles null
        if (!(obj instanceof LineInfo)) {
            return false;
        }

        // state check
        LineInfo other = (LineInfo) obj;
        return this.content.equals(other.content)
                && this.lineNumber == other.lineNumber
                && this.author.equals(other.author);
    }
}

