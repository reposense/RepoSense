package reposense.git;

import static reposense.system.CommandRunner.runCommand;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Contains git show related functionalities.
 * Git show shows various types of objects.
 */
public class GitShow {
    /**
     * Returns date of commit associated with commit hash.
     */
    public static Date getCommitDate(String root, String commitHash) throws RuntimeException {
        Path rootPath = Paths.get(root);
        String showCommand = "git show -s --format=%ci " + commitHash;
        String output = runCommand(rootPath, showCommand);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        try {
            return format.parse(output);
        } catch (ParseException pe) {
            throw new RuntimeException(pe);
        }
    }
}
