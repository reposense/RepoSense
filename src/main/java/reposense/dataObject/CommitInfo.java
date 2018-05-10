package reposense.dataObject;

import java.util.Date;


public class CommitInfo {
    private Author author;
    private String hash;
    private Date time;
    private String message;
    private int insertions;
    private int deletions;

    public CommitInfo(Author author, String hash, Date time, String message, int insertions, int deletions) {
        this.author = author;
        this.hash = hash;
        this.time = time;
        this.message = message;
        this.insertions = insertions;
        this.deletions = deletions;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getInsertions() {
        return insertions;
    }

    public void setInsertions(int insertions) {
        this.insertions = insertions;
    }

    public int getDeletions() {
        return deletions;
    }

    public void setDeletions(int deletions) {
        this.deletions = deletions;
    }
}
