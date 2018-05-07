package git;

import system.CommandRunner;
import util.FileUtil;


public class GitCloner {
    public static void downloadRepo(String organization, String repoName, String branchName) throws GitClonerException {
        FileUtil.deleteDirectory(FileUtil.getRepoDirectory(organization, repoName));


        try {
            System.out.println("cloning " + organization + "/" + repoName + "...");
            CommandRunner.cloneRepo(organization, repoName);
            System.out.println("cloning done!");
        } catch (RuntimeException e) {
            System.out.println("Error encountered in Git Cloning, will attempt to continue analyzing");
            e.printStackTrace();
            throw new GitClonerException(e);
            //Due to an unsolved bug on Windows Git, for some repository, Git Clone will return an error even
            // though the repo is cloned properly.
        }

        try {
            GitChecker.checkout(FileUtil.getRepoDirectory(organization, repoName), branchName);
        } catch (RuntimeException e) {
            System.out.println("Branch does not exist! Analyze terminated.");
            e.printStackTrace();
            throw new GitClonerException(e);
        }

    }
}
