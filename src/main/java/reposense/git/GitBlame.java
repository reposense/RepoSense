package reposense.git;

import static reposense.system.CommandRunner.runCommand;
import static reposense.util.StringsUtil.addQuote;

import java.nio.file.Path;
import java.nio.file.Paths;

import reposense.util.StringsUtil;

public class GitBlame {

    /**
     * Returns the raw git blame result for the {@code fileDirectory}, performed at the {@code root} directory.
     */
    public static String blame(String root, String fileDirectory) {
        Path rootPath = Paths.get(root);

        String blameCommand = "git blame -w --line-porcelain";
        blameCommand += " " + addQuote(fileDirectory);

        return StringsUtil.filterText(runCommand(rootPath, blameCommand), "(^author .*)|(^[0-9a-f]{40} .*)");
    }
}
