package git;

import system.CommandRunner;
import util.FileUtil;

/**
 * Created by matanghao1 on 10/7/17.
 */
public class GitCloner {
    public static void downloadRepo(String organization, String repoName, String branchName)
            {
        FileUtil.deleteDirectory(FileUtil.getRepoDirectory(organization,repoName));


        try {
            System.out.println("cloning "+ organization+"/"+repoName+"...");
            CommandRunner.cloneRepo(organization, repoName);
            System.out.println("cloning done!");
        } catch (RuntimeException e){
            System.out.println("repo does not exist in Github! Analyze terminated.");
            e.printStackTrace();
            throw e;
        }

        try{
            GitChecker.checkout(FileUtil.getRepoDirectory(organization,repoName), branchName);
        } catch (RuntimeException e){
            System.out.println("Branch does not exist! Analyze terminated.");
            e.printStackTrace();
            throw e;
        }

    }
}
