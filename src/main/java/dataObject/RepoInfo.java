package dataObject;

import java.util.ArrayList;

/**
 * Created by matanghao1 on 8/7/17.
 */
public class RepoInfo {


    private ArrayList<CommitInfo> commits;

    public RepoInfo(ArrayList<CommitInfo> commits) {
        this.commits = commits;
    }

    public ArrayList<CommitInfo> getCommits() {

        return commits;
    }

    public void setCommits(ArrayList<CommitInfo> commits) {
        this.commits = commits;
    }


}
