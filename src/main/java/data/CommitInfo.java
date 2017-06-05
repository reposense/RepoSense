package data;

import java.util.Date;

/**
 * Created by matanghao1 on 5/6/17.
 */
public class CommitInfo {
    private Author author;
    private String hash;
    private Date time;
    private String message;

    public CommitInfo(Author author, String hash, Date time, String message) {
        this.author = author;
        this.hash = hash;
        this.time = time;
        this.message = message;
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
}
