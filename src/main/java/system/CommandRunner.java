package system;

import util.Constants;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by matanghao1 on 28/5/17.
 */
public class CommandRunner {

    public static String gitLog(String root, Date fromDate, Date toDate){
        File rootFile = new File(root);
        String command = "git log --no-merges ";
        if (fromDate!=null){
            command+=" --since='" + Constants.GIT_LOG_DATE_FORMAT.format(fromDate) + "' ";
        }
        if (toDate != null){
            command+=" --until='" + Constants.GIT_LOG_DATE_FORMAT.format(toDate) + "' ";
        }
        command += " --pretty=format:\"%h|%aN|%ad|%s\" --date=iso --shortstat -- '*.java' | sed '/^$/d'";
        System.out.println(command);

        return runCommand(rootFile, command);
    }

    public static void checkOut(String root, String hash){
        File rootFile = new File(root);
        runCommand(rootFile, "git checkout "+hash);
    }

    public static String blameRaw(String root, String fileDirectory){
        File rootFile = new File(root);
        return runCommand(rootFile, "git blame " + fileDirectory + " -w -M -C --follow --find-copies-harder --line-porcelain | grep  \"^author \"");
    }

    public static String checkStyleRaw(String absoluteDirectory){
        File rootFile = new File(".");
        return runCommand(rootFile, "java -jar checkstyle-7.7-all.jar -c /google_checks.xml -f xml " + absoluteDirectory);
    }

    public static String cloneRepo(String org, String repoName){
        File rootFile = new File(Constants.REPOS_ADDRESS + "/" + org);
        if (!rootFile.exists()){
            rootFile.mkdir();
        }
        return runCommand(rootFile, "git clone " + Constants.GITHUB_URL_ROOT + org + "/" + repoName);
    }



    private static String runCommand(File directory, String command) {
        ProcessBuilder pb = new ProcessBuilder()
                .command(new String[] {"bash", "-c" , command})
                .directory(directory);
        Process p = null;
        try {
            p = pb.start();
        } catch (IOException e) {
            throw new RuntimeException("Error Creating Thread.");
        }
        StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream());
        StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream());
        outputGobbler.start();
        errorGobbler.start();
        int exit = 0;
        try {
            exit = p.waitFor();
            outputGobbler.join();
            errorGobbler.join();
        } catch (InterruptedException e) {
            throw new RuntimeException("Error Handling Thread.");
        }

        if (exit == 0) {
            return outputGobbler.getValue();
        }else{
            throw new RuntimeException("Error returned from command:\n"+errorGobbler.getValue());
        }
    }


}
