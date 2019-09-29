package reposense.authorship.model;

import java.util.Objects;

import reposense.model.Author;

/**
 * Stores the information of a line in a {@code FileInfo}.
 */
public class LineInfo {
    private int lineNumber;
    private Author author;
    private boolean isFullCredit;
    private String content;

    private transient boolean isTracked;

    public LineInfo(int lineNumber, String content) {
        this.lineNumber = lineNumber;
        this.content = content;

        isTracked = true;
        isFullCredit = false;
    }

    public Author getAuthor() {
        return author;
    }

    public boolean isFullCredit() {
        return isFullCredit;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public void setIsFullCredit(boolean isFullCredit) {
        this.isFullCredit = isFullCredit;
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
                && isTracked == otherLineInfo.isTracked;
    }
}

