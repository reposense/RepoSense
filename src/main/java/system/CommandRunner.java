package system;

import java.io.*;

/**
 * Created by matanghao1 on 28/5/17.
 */
public class CommandRunner {

    public static String blameRaw(String root, String fileDirectory){
        File rootFile = new File(root);
        return runCommand(rootFile, "git blame " + fileDirectory + " -M -C --follow --find-copies-harder --line-porcelain | grep  \"^author \"");
    }

    public static String runCommand(File directory, String command) {
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
