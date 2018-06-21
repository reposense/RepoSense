package reposense.authorship.extractor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import reposense.authorship.model.FileInfo;
import reposense.git.GitChecker;
import reposense.model.RepoConfiguration;
import reposense.system.LogsManager;

/**
 * Extracts out all the relevant {@code FileInfo} from the repository.
 */
public class FileInfoExtractor {
    private static final Logger logger = LogsManager.getLogger(FileInfoExtractor.class);

    /**
     * Extracts a list of relevant files given in {@code config}.
     */
    public static List<FileInfo> extractFileInfos(RepoConfiguration config) {
        logger.info("Extracting relevant file infos...");

        // checks out to the latest commit of the date range to ensure the FileInfo generated correspond to the
        // git blame file analyze output
        GitChecker.checkoutToDate(config.getRepoRoot(), config.getBranch(), config.getUntilDate());
        ArrayList<FileInfo> fileInfos = new ArrayList<>();
        getAllFileInfo(config, Paths.get(config.getRepoRoot()), fileInfos);
        fileInfos.sort(Comparator.comparing(FileInfo::getPath));

        return fileInfos;
    }

    /**
     * Traverses each file from the repo root directory, generates the {@code FileInfo} for each relevant file found
     * based on {@code config} and inserts it into {@code fileInfos}.
     */
    private static void getAllFileInfo(
            RepoConfiguration config, Path directory, ArrayList<FileInfo> fileInfos) {
        try (Stream<Path> pathStream = Files.list(directory)) {
            for (Path filePath : pathStream.collect(Collectors.toList())) {
                String relativePath = filePath.toString().substring(config.getRepoRoot().length());
                if (shouldIgnore(relativePath, config.getIgnoreDirectoryList())) {
                    return;
                }

                if (Files.isDirectory(filePath)) {
                    getAllFileInfo(config, filePath, fileInfos);
                }

                if (relativePath.endsWith(".java") || relativePath.endsWith(".adoc")) {
                    fileInfos.add(FileInfoGenerator.generateFileInfo(
                            config.getRepoRoot(), relativePath.replace('\\', '/')));
                }
            }
        } catch (IOException ioe) {
            logger.log(Level.SEVERE, "Error occured while extracing all relevant file infos.", ioe);
        }
    }

    /**
     * Returns true if any of the {@code String} in {@code ignoreList} is contained inside {@code name}.
     */
    private static boolean shouldIgnore(String name, List<String> ignoreList) {
        return ignoreList.stream().anyMatch(name::contains);
    }
}
