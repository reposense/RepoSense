package git;

import system.CommandRunner;
import util.Constants;
import util.FileUtil;

import java.io.File;
import java.io.IOException;

/**
 * Created by matanghao1 on 10/7/17.
 */
public class GitCloner {
    public static void downloadRepo(String organization, String repoName, String branchName)
            {
        FileUtil.deleteDirectory(FileUtil.getRepoDirectory(organization,repoName));


        try {
            CommandRunner.cloneRepo(organization, repoName);
            System.out.println("cloning done!");
        } catch (RuntimeException e){
            throw new RuntimeException("Repo does not exist!");
        }

        try{
            GitChecker.checkout(FileUtil.getRepoDirectory(organization,repoName), branchName);
        } catch (RuntimeException e){
            throw new RuntimeException("Branch does not exist!");
        }

    }
}
