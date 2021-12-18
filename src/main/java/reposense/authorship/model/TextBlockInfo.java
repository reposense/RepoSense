package reposense.authorship.model;

import reposense.model.Author;

import java.util.*;

public class TextBlockInfo {
    private int startLineNumber;
    private int endLineNumber;
    private HashMap<Author, Integer> contributionMap;
    private HashMap<Author, Integer> absoluteContributionMap = null;
    private ArrayList<LineInfo> lines;
    private Date lastModifiedDate;

    private transient boolean isTracked;

    public TextBlockInfo(int startLineNumber, int endLineNumber, ArrayList<LineInfo> lines) {
        this.startLineNumber = startLineNumber;
        this.endLineNumber = endLineNumber;
        this.lines = lines;

        isTracked = true;
    }

    public TextBlockInfo() {
        this.lines = new ArrayList<>();
        this.contributionMap = new HashMap<>();

        isTracked = true;
    }

    public HashMap<Author, Integer> getContributionMap() {
        return contributionMap;
    }

    public HashMap<Author, Integer> getAbsoluteContributionMap() {
        if (this.absoluteContributionMap == null) {
            int sum = 0;
            Iterator<Map.Entry<Author, Integer>> iterator = contributionMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Author, Integer> element = iterator.next();
                sum += element.getValue();
            }

            int totalLines = endLineNumber - startLineNumber;
            int finalSum = sum;
            contributionMap.forEach((author, integer) ->
                contributionMap.put(author, (integer * totalLines) / finalSum));
        }
        return this.absoluteContributionMap;

    }

    public void setContributionMap(HashMap<Author, Integer> contributionMap) {
        this.contributionMap = contributionMap;
    }

    public int getStartLineNumber() {
        return startLineNumber;
    }

    public void setStartLineNumber(int startLineNumber) {
        this.startLineNumber = startLineNumber;
    }

    public int getEndLineNumber() {
        return endLineNumber;
    }

    public void setEndLineNumber(int endLineNumber) {
        this.endLineNumber = endLineNumber;
    }

    public void setTracked(boolean isTracked) {
        this.isTracked = isTracked;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public boolean isTracked() {
        return isTracked;
    }

    public void addLine(LineInfo line) {
        this.lines.add(line);
    }

    public void removeLine(LineInfo line) {
        this.lines.remove(line);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof TextBlockInfo)) {
            return false;
        }

        TextBlockInfo otherTextBlockInfo = (TextBlockInfo) other;
        return startLineNumber == otherTextBlockInfo.startLineNumber
                && endLineNumber == otherTextBlockInfo.endLineNumber
                && contributionMap.equals(otherTextBlockInfo.contributionMap)
                && absoluteContributionMap.equals(otherTextBlockInfo.absoluteContributionMap)
                && lines.equals(otherTextBlockInfo.lines)
                && isTracked == otherTextBlockInfo.isTracked
                && ((lastModifiedDate == null && otherTextBlockInfo.lastModifiedDate == null)
                || (lastModifiedDate.equals(otherTextBlockInfo.lastModifiedDate)));
    }

}
