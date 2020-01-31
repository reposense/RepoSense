package reposense.authorship;

import reposense.authorship.model.BinaryFileInfo;
import reposense.git.GitCheckout;
import reposense.git.GitDiff;
import reposense.git.exception.CommitNotFoundException;
import reposense.model.RepoConfiguration;
import reposense.system.LogsManager;
import reposense.util.FileUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Extracts out all the relevant {@code BinaryFileInfo} from the repository.
 */
public class BinaryFileInfoExtractor {
    private static final Logger logger = LogsManager.getLogger(BinaryFileInfoExtractor.class);
    private static final String BINARY_FILE_LINE_DIFF_RESULT = "-\t-\t";
    private static final String MESSAGE_START_EXTRACTING_BINARY_FILE_INFO = "Extracting relevant binary file info from %s (%s)...";

    public static List<BinaryFileInfo> extractBinaryFileInfos(RepoConfiguration config) {
        logger.info(String.format(MESSAGE_START_EXTRACTING_BINARY_FILE_INFO, config.getLocation(), config.getBranch()));

        List<BinaryFileInfo> binaryFileInfos = new ArrayList<>();

        // checks out to the latest commit of the date range to ensure the FileInfo generated correspond to the
        // git blame file analyze output
        try {
            GitCheckout.checkoutDate(config.getRepoRoot(), config.getBranch(), config.getUntilDate());
        } catch (CommitNotFoundException cnfe) {
            return binaryFileInfos;
        }

        binaryFileInfos = getAllBinaryFileInfo(config);

        binaryFileInfos.sort(Comparator.comparing(BinaryFileInfo::getPath));
        return binaryFileInfos;
    }

    public static List<BinaryFileInfo> getAllBinaryFileInfo(RepoConfiguration config) {
        Set<Path> nonBinaryFilesList = getBinaryFilesList(config);
        List<BinaryFileInfo> binaryFileInfos = new ArrayList<>();
        for (Path relativePath : nonBinaryFilesList) {
            if (config.getFileTypeManager().isInsideWhitelistedFormats(relativePath.toString())) {
                binaryFileInfos.add(generateBinaryFileInfo(relativePath.toString()));
            }
        }
        return binaryFileInfos;
    }

    private static BinaryFileInfo generateBinaryFileInfo(String relativePath) {
        return new BinaryFileInfo(relativePath);
    }

    /**
     * Returns a {@code Set} of binary files for the repo {@code repoConfig}.
     */
    public static Set<Path> getBinaryFilesList(RepoConfiguration repoConfig) {
        List<String> modifiedFileList = GitDiff.getModifiedFilesList(Paths.get(repoConfig.getRepoRoot()));

        // Gets rid of binary files and files with invalid directory name.
        return modifiedFileList.stream()
                .filter(file -> file.startsWith(BINARY_FILE_LINE_DIFF_RESULT))
                .map(rawNonBinaryFile -> rawNonBinaryFile.split("\t")[2])
                .filter(FileUtil::isValidPath)
                .map(filteredFile -> Paths.get(filteredFile))
                .collect(Collectors.toCollection(HashSet::new));
    }
}
