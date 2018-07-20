package reposense.authorship;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import reposense.authorship.model.FileInfo;
import reposense.authorship.model.LineInfo;
import reposense.git.GitChecker;
import reposense.model.RepoConfiguration;
import reposense.system.CommandRunner;
import reposense.system.LogsManager;

/**
 * Extracts out all the relevant {@code FileInfo} from the repository.
 */
public class FileInfoExtractor {
    private static final Logger logger = LogsManager.getLogger(FileInfoExtractor.class);

    private static final String DIFF_FILE_CHUNK_SEPARATOR = "\ndiff --git a/.*\n";
    private static final String BINARY_FILE_SYMBOL = "\nBinary files ";
    private static final String FILE_DELETED_METADATA = "deleted file mode 100644\n";
    private static final String LINE_CHUNKS_SEPARATOR = "\n@@ ";
    private static final String LINE_INSERTED_SYMBOL = "+";

    private static final int CHUNK_HEADER_LINE_INDEX = 0;
    private static final int CHUNK_STARTING_LINE_GROUP_INDEX = 2;
    private static final int FILE_PATH_GROUP_INDEX = 2;

    private static final Pattern CHUNK_HEADER_PATTERN = Pattern.compile("-(\\d+)[,\\d]* \\+(\\d+).*");
    private static final Pattern FILE_PATH_PATTERN = Pattern.compile("\n(\\+\\+\\+ b/)(.*)\n");

    /**
     * Extracts a list of relevant files given in {@code config}.
     */
    public static List<FileInfo> extractFileInfos(RepoConfiguration config) {
        logger.info("Extracting relevant file infos " + config.getDisplayName() + "...");

        // checks out to the latest commit of the date range to ensure the FileInfo generated correspond to the
        // git blame file analyze output
        GitChecker.checkoutToDate(config.getRepoRoot(), config.getBranch(), config.getUntilDate());
        List<FileInfo> fileInfos = new ArrayList<>();
        String lastCommitHash = CommandRunner.getCommitHashBeforeDate(
                config.getRepoRoot(), config.getBranch(), config.getSinceDate());

        if (!lastCommitHash.isEmpty()) {
            fileInfos = getEditedFileInfos(config, lastCommitHash);
        } else {
            getAllFileInfo(config, Paths.get(config.getRepoRoot()), fileInfos);
        }

        fileInfos.sort(Comparator.comparing(FileInfo::getPath));
        return fileInfos;
    }

    /**
     * Generates a list of relevant {@code FileInfo} for all files that were edited in between the current
     * commit and the {@code lastCommitHash} commit, marks each {@code LineInfo} for each {@code FileInfo} on
     * whether they have been inserted within the commit range or not, and returns it.
     */
    public static List<FileInfo> getEditedFileInfos(RepoConfiguration config, String lastCommitHash) {
        List<FileInfo> fileInfos = new ArrayList<>();

        String diffResult = CommandRunner.diffCommit(config.getRepoRoot(), lastCommitHash);

        // no diff between the 2 commits, return an empty list
        if (diffResult.isEmpty()) {
            return fileInfos;
        }

        String[] fileDiffResultList = diffResult.split(DIFF_FILE_CHUNK_SEPARATOR);

        for (String fileDiffResult : fileDiffResultList) {
            // file deleted or is a binary file, skip it
            if (fileDiffResult.startsWith(FILE_DELETED_METADATA) || fileDiffResult.contains(BINARY_FILE_SYMBOL)) {
                continue;
            }

            String filePath = getFilePathFromDiffPattern(fileDiffResult);
            if (isFormatInsideWhiteList(filePath, config.getFormats())) {
                FileInfo currentFileInfo = generateFileInfo(config.getRepoRoot(), filePath);
                setLinesToTrack(currentFileInfo, fileDiffResult);
                fileInfos.add(currentFileInfo);
            }
        }

        return fileInfos;
    }

    /**
     * Analyzes the {@code fileDiffResult} and marks each {@code LineInfo} in {@code FileInfo} on whether they were
     * inserted in between the commit range.
     */
    private static void setLinesToTrack(FileInfo fileInfo, String fileDiffResult) {
        String[] lineChangedChunks = fileDiffResult.split(LINE_CHUNKS_SEPARATOR);
        List<LineInfo> lineInfos = fileInfo.getLines();
        int fileLinePointer = 0;

        for (int chunkIndex = 1; chunkIndex < lineChangedChunks.length; chunkIndex++) {
            String lineChangedChunk = lineChangedChunks[chunkIndex];
            String[] linesChangedInChunk = lineChangedChunk.split("\n");
            int chunkStartingLineNumber = getChunkStartingLineNumber(linesChangedInChunk[CHUNK_HEADER_LINE_INDEX]);

            // mark all untouched lines between chunks as untracked
            while (fileLinePointer < chunkStartingLineNumber - 1) {
                lineInfos.get(fileLinePointer++).setTracked(false);
            }

            for (int lineIndex = 1; lineIndex < linesChangedInChunk.length; lineIndex++) {
                String lineChanged = linesChangedInChunk[lineIndex];
                // set line added to be tracked
                if (lineChanged.startsWith(LINE_INSERTED_SYMBOL)) {
                    lineInfos.get(fileLinePointer++).setTracked(true);
                }
            }
        }

        // set all remaining lines in file that were untouched to be untracked
        while (fileLinePointer < lineInfos.size()) {
            lineInfos.get(fileLinePointer++).setTracked(false);
        }
    }

    /**
     * Traverses each file from the repo root directory, generates the {@code FileInfo} for each relevant file found
     * based on {@code config} and inserts it into {@code fileInfos}.
     */
    private static void getAllFileInfo(RepoConfiguration config, Path directory, List<FileInfo> fileInfos) {
        try (Stream<Path> pathStream = Files.list(directory)) {
            for (Path filePath : pathStream.collect(Collectors.toList())) {
                String relativePath = filePath.toString().substring(config.getRepoRoot().length());
                if (shouldIgnore(relativePath, config.getIgnoreDirectoryList())) {
                    return;
                }

                if (Files.isDirectory(filePath)) {
                    getAllFileInfo(config, filePath, fileInfos);
                }

                if (isFormatInsideWhiteList(relativePath, config.getFormats())) {
                    fileInfos.add(generateFileInfo(config.getRepoRoot(), relativePath));
                }
            }
        } catch (IOException ioe) {
            logger.log(Level.SEVERE, "Error occured while extracing all relevant file infos.", ioe);
        }
    }

    /**
     * Generates and returns a {@code FileInfo} with a list of {@code LineInfo} for each line content in the
     * {@code relativePath} file.
     */
    public static FileInfo generateFileInfo(String repoRoot, String relativePath) {
        FileInfo fileInfo = new FileInfo(relativePath.replace('\\', '/'));
        Path path = Paths.get(repoRoot, fileInfo.getPath());

        try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            int lineNum = 1;
            while ((line = br.readLine()) != null) {
                fileInfo.addLine(new LineInfo(lineNum++, line));
            }
        } catch (IOException ioe) {
            logger.log(Level.SEVERE, ioe.getMessage(), ioe);
        }
        return fileInfo;
    }

    /**
     * Returns true if any of the {@code String} in {@code ignoreList} is contained inside {@code name}.
     */
    private static boolean shouldIgnore(String name, List<String> ignoreList) {
        return ignoreList.stream().anyMatch(name::contains);
    }

    /**
     * Returns true if the {@code relativePath}'s file type is inside {@code formatsWhiteList}.
     */
    private static boolean isFormatInsideWhiteList(String relativePath, List<String> formatsWhiteList) {
        return formatsWhiteList.stream().anyMatch(format -> relativePath.endsWith("." + format));
    }

    /**
     * Returns the file path by matching the pattern inside {@code fileDiffResult}.
     */
    private static String getFilePathFromDiffPattern(String fileDiffResult) {
        Matcher filePathMatcher = FILE_PATH_PATTERN.matcher(fileDiffResult);

        if (!filePathMatcher.find()) {
            throw new AssertionError("Should not have error matching file path pattern inside file diff result!");
        }

        return filePathMatcher.group(FILE_PATH_GROUP_INDEX);
    }

    /**
     * Returns the chunk's starting line number, within the file diff result, by matching the pattern inside
     * {@code chunkHeader}.
     */
    private static int getChunkStartingLineNumber(String chunkHeader) {
        Matcher chunkHeaderMatcher = CHUNK_HEADER_PATTERN.matcher(chunkHeader);

        if (!chunkHeaderMatcher.find()) {
            throw new AssertionError("Should not have error matching line number pattern inside chunk header!");
        }

        return Integer.parseInt(chunkHeaderMatcher.group(CHUNK_STARTING_LINE_GROUP_INDEX));
    }
}
