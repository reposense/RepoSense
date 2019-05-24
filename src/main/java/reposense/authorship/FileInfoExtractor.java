package reposense.authorship;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
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
import reposense.git.GitCheckout;
import reposense.git.GitDiff;
import reposense.git.GitRevList;
import reposense.git.exception.CommitNotFoundException;
import reposense.model.Format;
import reposense.model.RepoConfiguration;
import reposense.system.LogsManager;

/**
 * Extracts out all the relevant {@code FileInfo} from the repository.
 */
public class FileInfoExtractor {
    private static final Logger logger = LogsManager.getLogger(FileInfoExtractor.class);
    private static final String MESSAGE_START_EXTRACTING_FILE_INFO = "Extracting relevant file info from %s (%s)...";

    private static final String DIFF_FILE_CHUNK_SEPARATOR = "\ndiff --git a/.*\n";
    private static final String LINE_CHUNKS_SEPARATOR = "\n@@ ";
    private static final String LINE_INSERTED_SYMBOL = "+";
    private static final String STARTING_LINE_NUMBER_GROUP_NAME = "startingLineNumber";
    private static final String FILE_CHANGED_GROUP_NAME = "filePath";
    private static final String FILE_DELETED_SYMBOL = "/dev/null";
    private static final String MATCH_GROUP_FAIL_MESSAGE_FORMAT = "Failed to match the %s group for:\n%s";
    private static final String INVALID_FILE_PATH_MESSAGE_FORMAT = "Invalid file path %s provided, skipping this file.";
    private static final String GIT_DIRECTORY = ".git";

    private static final int LINE_CHANGED_HEADER_INDEX = 0;

    private static final Pattern STARTING_LINE_NUMBER_PATTERN = Pattern.compile(
            "-(\\d)+(,)?(\\d)* \\+(?<startingLineNumber>\\d+)(,)?(\\d)* @@");
    private static final Pattern FILE_CHANGED_PATTERN = Pattern.compile("\n(\\+){3} b?/(?<filePath>.*)\n");

    /**
     * Extracts a list of relevant files given in {@code config}.
     */
    public static List<FileInfo> extractFileInfos(RepoConfiguration config) {
        logger.info(String.format(MESSAGE_START_EXTRACTING_FILE_INFO, config.getLocation(), config.getBranch()));

        List<FileInfo> fileInfos = new ArrayList<>();

        // checks out to the latest commit of the date range to ensure the FileInfo generated correspond to the
        // git blame file analyze output
        try {
            GitCheckout.checkoutDate(config.getRepoRoot(), config.getBranch(), config.getUntilDate());
        } catch (CommitNotFoundException cnfe) {
            return fileInfos;
        }
        String lastCommitHash = GitRevList.getCommitHashBeforeDate(
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
        String fullDiffResult = GitDiff.diffCommit(config.getRepoRoot(), lastCommitHash);

        // no diff between the 2 commits, return an empty list
        if (fullDiffResult.isEmpty()) {
            return fileInfos;
        }

        String[] fileDiffResultList = fullDiffResult.split(DIFF_FILE_CHUNK_SEPARATOR);

        for (String fileDiffResult : fileDiffResultList) {
            Matcher filePathMatcher = FILE_CHANGED_PATTERN.matcher(fileDiffResult);

            // diff result does not have the markers to indicate that file has any line changes, skip it
            if (!filePathMatcher.find()) {
                continue;
            }

            String filePath = filePathMatcher.group(FILE_CHANGED_GROUP_NAME);

            // file is deleted, skip it as well
            if (filePath.equals(FILE_DELETED_SYMBOL)) {
                continue;
            }

            if (Format.isInsideWhiteList(filePath, config.getFormats())
                    || config.isFormatRestrictionDisabled()) {
                try {
                    FileInfo currentFileInfo = generateFileInfo(config.getRepoRoot(), filePath);
                    setLinesToTrack(currentFileInfo, fileDiffResult);
                    fileInfos.add(currentFileInfo);
                } catch (InvalidPathException ipe) {
                    logger.warning(String.format(INVALID_FILE_PATH_MESSAGE_FORMAT, filePath));
                }
            }
        }

        return fileInfos;
    }

    /**
     * Analyzes the {@code fileDiffResult} and marks each {@code LineInfo} in {@code FileInfo} on whether they were
     * inserted in between the commit range.
     */
    private static void setLinesToTrack(FileInfo fileInfo, String fileDiffResult) {
        String[] linesChangedChunk = fileDiffResult.split(LINE_CHUNKS_SEPARATOR);
        List<LineInfo> lineInfos = fileInfo.getLines();
        int fileLinePointer = 0;

        // skips the header, index starts from 1
        for (int sectionIndex = 1; sectionIndex < linesChangedChunk.length; sectionIndex++) {
            String linesChangedInSection = linesChangedChunk[sectionIndex];
            String[] linesChanged = linesChangedInSection.split("\n");
            int startingLineNumber = getStartingLineNumber(linesChanged[LINE_CHANGED_HEADER_INDEX]);

            // mark all untouched lines between sections as untracked
            while (fileLinePointer < startingLineNumber - 1) {
                lineInfos.get(fileLinePointer++).setTracked(false);
            }

            // skips the header, index starts from 1
            for (int lineIndex = 1; lineIndex < linesChanged.length; lineIndex++) {
                String lineChanged = linesChanged[lineIndex];
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
                if (Files.isDirectory(filePath) && relativePath.equals(GIT_DIRECTORY)) {
                    continue;
                }

                if (Files.isDirectory(filePath)) {
                    getAllFileInfo(config, filePath, fileInfos);
                    continue;
                }

                if (Format.isInsideWhiteList(relativePath, config.getFormats())
                        || config.isFormatRestrictionDisabled()) {
                    try {
                        fileInfos.add(generateFileInfo(config.getRepoRoot(), relativePath));
                    } catch (InvalidPathException ipe) {
                        logger.warning(String.format(INVALID_FILE_PATH_MESSAGE_FORMAT, filePath));
                    }
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
     * Returns the starting line changed number, within the file diff result, by matching the pattern inside
     * {@code linesChanged}.
     */
    private static int getStartingLineNumber(String linesChanged) {
        Matcher chunkHeaderMatcher = STARTING_LINE_NUMBER_PATTERN.matcher(linesChanged);

        if (!chunkHeaderMatcher.find()) {
            logger.severe(String.format(MATCH_GROUP_FAIL_MESSAGE_FORMAT, "line changed", linesChanged));
            throw new AssertionError("Should not have error matching line number pattern inside chunk header!");
        }

        return Integer.parseInt(chunkHeaderMatcher.group(STARTING_LINE_NUMBER_GROUP_NAME));
    }
}
