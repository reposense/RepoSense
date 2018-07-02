package reposense.authorship;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
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

    private static final String DIFF_FILE_CHUNK_SEPARATOR = "\ndiff --git a/";
    private static final String COMPARED_FILE_PATH_SEPARATOR = " b/";
    private static final String LINE_CHUNKS_SEPARATOR = "\n@@ ";
    private static final String FILE_DELETED_METADATA = "deleted file mode 100644";
    private static final String LINE_INSERTED_SYMBOL = "+";
    private static final String LINE_DELETED_SYMBOL = "-";

    private static final int COMPARED_FILE_LINE_INDEX = 0;
    private static final int CHUNK_HEADER_LINE_INDEX = 0;
    private static final int FILE_COMPARED_BEFORE_INDEX = 0;
    private static final int FILE_COMPARED_AFTER_INDEX = 1;
    private static final int DIFF_FILE_METADATA_INDEX = 1;
    private static final int CHUNK_STARTING_LINE_NUMBER_INDEX = 1;

    private static final Pattern CHUNK_HEADER_PATTERN = Pattern.compile("-(\\d+)[,\\d]* \\+(\\d+).*");

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
     * Updates all the {@code FileInfo} content in {@code fileInfos} to correspond to the latest commit,
     * and returns that list of updated {@code FileInfo}s.
     */
    public static List<FileInfo> updateFileInfos(RepoConfiguration config, List<FileInfo> fileInfos) {
        logger.info("Updating relevant file infos...");

        TreeMap<String, FileInfo> map = new TreeMap<>();
        fileInfos.forEach(fileInfo -> map.put(fileInfo.getPath(), fileInfo));

        String diffResult = CommandRunner.diffCommit(config.getRepoRoot(), config.getLastCommitHash());
        String[] fileDiffResultList = diffResult.split(DIFF_FILE_CHUNK_SEPARATOR);

        for (String fileDiffResult : fileDiffResultList) {
            String[] fileDiffResultLines = fileDiffResult.split("\n");
            String[] filesCompared = fileDiffResultLines[COMPARED_FILE_LINE_INDEX].split(COMPARED_FILE_PATH_SEPARATOR);
            String filePathBefore = filesCompared[FILE_COMPARED_BEFORE_INDEX];
            String filePathAfter = filesCompared[FILE_COMPARED_AFTER_INDEX];

            if (isFileFormatInsideWhiteList(filePathBefore, config.getFileFormats())) {
                FileInfo currentFileInfo = map.get(filePathBefore);

                // new file, generate whole file info
                if (!map.containsKey(filePathBefore)) {
                    map.put(filePathAfter, generateFileInfo(config.getRepoRoot(), filePathAfter));
                    continue;
                }

                // file deleted, remove file info
                if (fileDiffResultLines[DIFF_FILE_METADATA_INDEX].equals(FILE_DELETED_METADATA)) {
                    map.remove(filePathBefore);
                    continue;
                }

                // file renamed, reset the file info's path
                if (!filePathBefore.equals(filePathAfter)) {
                    currentFileInfo.setPath(filePathAfter);
                }

                updateFileInfo(currentFileInfo, fileDiffResult);

                // if file extension changed to one that is white-listed, generate the new file info
            } else if (isFileFormatInsideWhiteList(filePathAfter, config.getFileFormats())) {
                map.put(filePathAfter, generateFileInfo(config.getRepoRoot(), filePathAfter));
            }
        }

        return new ArrayList<>(map.values());
    }

    /**
     * Analyzes the {@code diffFileResult} and updates the {@code fileInfo} content to the latest commit.
     */
    private static void updateFileInfo(FileInfo fileInfo, String diffFileResult) {
        String[] lineChangedChunks = diffFileResult.split(LINE_CHUNKS_SEPARATOR);
        List<LineInfo> oldLineInfos = fileInfo.getLines();
        List<LineInfo> updatedLineInfos = new ArrayList<>();
        int oldFileLinePointer = 0;

        for (int chunkIndex = 1; chunkIndex < lineChangedChunks.length; chunkIndex++) {
            String lineChangedChunk = lineChangedChunks[chunkIndex];
            String[] linesChangedInChunk = lineChangedChunk.split("\n");
            Matcher chunkHeaderMatcher = CHUNK_HEADER_PATTERN.matcher(linesChangedInChunk[CHUNK_HEADER_LINE_INDEX]);

            if (!chunkHeaderMatcher.find()) {
                throw new AssertionError("Error while parsing git diff results.");
            }

            // add in all old line infos between chunks that were not changed between the 2 file versions
            int chunkStartingLineNumber = Integer.parseInt(chunkHeaderMatcher.group(CHUNK_STARTING_LINE_NUMBER_INDEX));
            while (oldFileLinePointer < chunkStartingLineNumber) {
                updatedLineInfos.add(oldLineInfos.get(oldFileLinePointer++));
                updatedLineInfos.get(updatedLineInfos.size() - 1).setLineNumber(updatedLineInfos.size());
            }

            boolean firstDeletedLine = true;
            for (int lineIndex = 1; lineIndex < linesChangedInChunk.length; lineIndex++) {
                String lineChanged = linesChangedInChunk[lineIndex];
                if (lineChanged.startsWith(LINE_DELETED_SYMBOL)) {
                    // first deleted line was added in previously, remove it
                    if (firstDeletedLine) {
                        firstDeletedLine = false;
                        updatedLineInfos.remove(updatedLineInfos.size() - 1);

                        // skip rest of the subsequent deleted lines
                    } else {
                        oldFileLinePointer++;
                    }
                }

                // new line inserted, construct new line info and insert
                if (lineChanged.startsWith(LINE_INSERTED_SYMBOL)) {
                    String content = lineChanged.substring(1);
                    updatedLineInfos.add(new LineInfo(updatedLineInfos.size() + 1, content));
                }
            }
        }

        // add in all remaining lines in the old file version
        while (oldFileLinePointer < oldLineInfos.size()) {
            updatedLineInfos.add(oldLineInfos.get(oldFileLinePointer++));
            updatedLineInfos.get(updatedLineInfos.size() - 1).setLineNumber(updatedLineInfos.size());
        }

        fileInfo.setLines(updatedLineInfos);
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

                if (isFileFormatInsideWhiteList(relativePath, config.getFileFormats())) {

                    fileInfos.add(generateFileInfo(config.getRepoRoot(), relativePath.replace('\\', '/')));
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
        FileInfo fileInfo = new FileInfo(relativePath);
        Path path = Paths.get(repoRoot, relativePath);
        try (BufferedReader br = new BufferedReader(Files.newBufferedReader(path))) {
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
     * Returns true if the {@code relativePath}'s file type is inside {@code fileFormatsWhiteList}.
     */
    private static boolean isFileFormatInsideWhiteList(String relativePath, List<String> fileFormatsWhiteList) {
        return fileFormatsWhiteList.stream().anyMatch(fileFormat -> relativePath.endsWith("." + fileFormat));
    }
}
