package dataObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by matanghao1 on 5/6/17.
 */
public class CommitInfo {
    private Author author;
    private String hash;
    private Date time;
    private String message;
    private ArrayList<FileInfo> fileinfos;
    private HashMap<Author, Integer> authorIssueMap;
    private HashMap<Author, Integer> authorContributionMap;

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

    public ArrayList<FileInfo> getFileinfos() {
        return fileinfos;
    }

    public void setFileinfos(ArrayList<FileInfo> fileinfos) {
        this.fileinfos = fileinfos;
    }

    public HashMap<Author, Integer> getAuthorIssueMap() {
        return authorIssueMap;
    }

    public void setAuthorIssueMap(HashMap<Author, Integer> authorIssueMap) {
        this.authorIssueMap = authorIssueMap;
    }

    public HashMap<Author, Integer> getAuthorContributionMap() {
        return authorContributionMap;
    }

    public void setAuthorContributionMap(HashMap<Author, Integer> authorContributionMap) {
        this.authorContributionMap = authorContributionMap;
    }
}
