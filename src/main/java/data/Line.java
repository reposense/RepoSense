package data;

/**
 * Created by matanghao1 on 29/5/17.
 */
public class Line {
    public Line(int lineNumber,String authorName) {
        this.lineNumber = lineNumber;
        this.author = new Author(authorName);
    }

    private int lineNumber;
    private Author author;
    private String issue;


    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
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
}
