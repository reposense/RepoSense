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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import reposense.authorship.model.FileInfo;
import reposense.authorship.model.LineInfo;
import reposense.git.GitCheckout;
import reposense.git.GitDiff;
import reposense.git.GitRevList;
import reposense.git.exception.CommitNotFoundException;
import reposense.model.RepoConfiguration;
import reposense.system.LogsManager;
import reposense.util.FileUtil;

/**
 * Extracts out all the relevant {@code FileInfo} from the repository.
 */
public class FileInfoExtractor {
    private static final Logger logger = LogsManager.getLogger(FileInfoExtractor.class);
    private static final String MESSAGE_START_EXTRACTING_FILE_INFO = "Extracting relevant file info from %s (%s)...";
    private static final String MESSAGE_INVALID_FILE_PATH = "\"%s\" is an invalid file path for current OS or "
            + "indicates a possible regex match issue. Skipping this directory.";
    private static final String MESSAGE_FILE_SIZE_LIMIT_EXCEEDED = "File \"%s\" has %dB size. The file size "
            + "limit is set at %dB. %s";
    private static final String MESSAGE_FILE_ANALYSIS_SKIPPED = "Skipping analysis of this file...";
    private static final String MESSAGE_FILE_EXCLUDED_FROM_REPORT = "Exact line diffs will be excluded from report...";

    private static final String DIFF_FILE_CHUNK_SEPARATOR = "\ndiff --git \"?\'?a/.*\n";
    private static final String LINE_CHUNKS_SEPARATOR = "\n@@ ";
    private static final String LINE_INSERTED_SYMBOL = "+";
    private static final String STARTING_LINE_NUMBER_GROUP_NAME = "startingLineNumber";
    private static final String FILE_CHANGED_GROUP_NAME = "filePath";
    private static final String FILE_DELETED_SYMBOL = "dev/null";
    private static final String MATCH_GROUP_FAIL_MESSAGE_FORMAT = "Failed to match the %s group for:\n%s";
    private static final String BINARY_FILE_LINE_DIFF_RESULT = "-\t-\t";

    private static final int LINE_CHANGED_HEADER_INDEX = 0;

    private static final Pattern STARTING_LINE_NUMBER_PATTERN = Pattern.compile(
            "-(\\d)+(,)?(\\d)* \\+(?<startingLineNumber>\\d+)(,)?(\\d)* @@");
    private static final Pattern FILE_CHANGED_PATTERN = Pattern.compile("\n(\\+){3} b?/(?<filePath>.*?)\t?\n");

    /**
     * Extracts a list of relevant non-binary files given in {@code config}.
     */
    public List<FileInfo> extractTextFileInfos(RepoConfiguration config) {
        logger.info(String.format(MESSAGE_START_EXTRACTING_FILE_INFO, config.getLocation(), config.getBranch()));

        List<FileInfo> fileInfos = new ArrayList<>();

        // checks out to the latest commit of the date range to ensure the FileInfo generated correspond to the
        // git blame file analyze output
        try {
            GitCheckout.checkoutDate(config.getRepoRoot(), config.getBranch(), config.getUntilDate(),
                    config.getZoneId());
        } catch (CommitNotFoundException cnfe) {
            return fileInfos;
        }
        String lastCommitHash = GitRevList.getCommitHashUntilDate(
                config.getRepoRoot(), config.getBranch(), config.getSinceDate(), config.getZoneId());

        fileInfos = (lastCommitHash.isEmpty())
                ? getAllFileInfo(config, false)
                : getEditedFileInfos(config, lastCommitHash);

        fileInfos.sort(Comparator.comparing(FileInfo::getPath));
        return fileInfos;
    }

    /**
     * Extracts a list of relevant binary files given in {@code config}.
     */
    public List<FileInfo> extractBinaryFileInfos(RepoConfiguration config) {
        List<FileInfo> binaryFileInfos = getAllFileInfo(config, true);
        binaryFileInfos.sort(Comparator.comparing(FileInfo::getPath));
        return binaryFileInfos;
    }

    /**
     * Returns a list of {@link FileInfo}s for all files in the repo with lines marked indicating if they were edited
     * in between the current commit and the commit given by {@code lastCommitHash}.
     * The repo is given by {@code config}.
     */
    public List<FileInfo> getEditedFileInfos(RepoConfiguration config, String lastCommitHash) {
        List<FileInfo> fileInfos = new ArrayList<>();
        String fullDiffResult = GitDiff.diffCommit(config.getRepoRoot(), lastCommitHash);
        // no diff between the 2 commits, return an empty list
        if (fullDiffResult.isEmpty()) {
            return fileInfos;
        }

        String[] fileDiffResultList = fullDiffResult.split(DIFF_FILE_CHUNK_SEPARATOR);
        Set<Path> textFilesSet = getFiles(config, false);

        for (String fileDiffResult : fileDiffResultList) {
            Matcher filePathMatcher = FILE_CHANGED_PATTERN.matcher(fileDiffResult);

            // diff result does not have the markers to indicate that file has any line changes, skip it
            if (!filePathMatcher.find()) {
                continue;
            }

            String filePath = filePathMatcher.group(FILE_CHANGED_GROUP_NAME);

            if (filePath.equals(FILE_DELETED_SYMBOL) // file is deleted, skip it as well
                    || !isValidTextFile(filePath, textFilesSet)
                    || !config.getFileTypeManager().isInsideWhitelistedFormats(filePath)
                    || FileUtil.isFileIgnoredByGlob(config, Paths.get(filePath))) {
                continue;
            }

            FileInfo currentFileInfo = generateFileInfo(config, filePath);
            setLinesToTrack(currentFileInfo, fileDiffResult);
            if (currentFileInfo.isFileAnalyzed()) {
                fileInfos.add(currentFileInfo);
            }
        }

        return fileInfos;
    }

    /**
     * Returns a {@link Set} of non-binary files for the repo {@code repoConfig}
     * if {@code isBinaryFiles} is set to `false`.
     * Otherwise, returns a {@link Set} of binary files for the repo {@code repoConfig}.
     */
    public Set<Path> getFiles(RepoConfiguration repoConfig, boolean isBinaryFile) {
        List<String> modifiedFileList = GitDiff.getModifiedFilesList(Paths.get(repoConfig.getRepoRoot()));

        // Gets rid of files with invalid directory name and filters by the {@code isBinaryFile} flag
        return modifiedFileList.stream()
                .filter(file -> isBinaryFile == file.startsWith(BINARY_FILE_LINE_DIFF_RESULT))
                .map(file -> file.split("\t")[2])
                .filter(FileUtil::isValidPathWithLogging)
                .map(filteredFile -> Paths.get(filteredFile))
                .collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * Analyzes the {@code fileDiffResult} and marks each {@link LineInfo} in {@code fileInfo} on whether they were
     * inserted in between the commit range.
     */
    private void setLinesToTrack(FileInfo fileInfo, String fileDiffResult) {
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
     * Traverses each file from the repo root directory, generates the {@link FileInfo} for each relevant file found
     * based on {@code config} and inserts it into {@code fileInfos}.
     * Adds binary files to {@link List} if {@code isBinaryFiles} is true. Otherwise, adds non-binary files
     * to {@link List}.
     */
    private List<FileInfo> getAllFileInfo(RepoConfiguration config, boolean isBinaryFiles) {
        List<FileInfo> fileInfos = new ArrayList<>();
        Set<Path> files = getFiles(config, isBinaryFiles);

        for (Path relativePath : files) {
            if (!config.getFileTypeManager().isInsideWhitelistedFormats(relativePath.toString())
                    || FileUtil.isFileIgnoredByGlob(config, relativePath)) {
                continue;
            }

            FileInfo fileInfo = (isBinaryFiles)
                    ? new FileInfo(relativePath.toString())
                    : generateFileInfo(config, relativePath.toString());

            if (fileInfo.isFileAnalyzed()) {
                fileInfos.add(fileInfo);
            }
        }
        return fileInfos;
    }

    /**
     * Returns a {@link FileInfo} with a list of {@link LineInfo} for each line content in the
     * file located in the repository given by {@code config}/{@code relativePath}.
     */
    public FileInfo generateFileInfo(RepoConfiguration config, String relativePath) {
        return generateFileInfo(config.getRepoRoot(), relativePath, config.getFileSizeLimit(),
            config.isFileSizeLimitIgnored(), config.isIgnoredFileAnalysisSkipped());
    }

    /**
     * Returns a {@link FileInfo} with a list of {@link LineInfo} for each line content in the
     * file located at the {@link Path} given by {@code repoRoot}/{@code relativePath}. {@code fileSizeLimit} and
     * {@code ignoreFileSizeLimit} are used to set whether the file size limit is exceeding. {@link LineInfo}s are
     * not included in {@link FileInfo} if  {@code skipIgnoredFileAnalysis} is true and file size limit is exceeding.
     */
    public FileInfo generateFileInfo(String repoRoot, String relativePath, long fileSizeLimit,
            boolean ignoreFileSizeLimit, boolean skipIgnoredFileAnalysis) {
        FileInfo fileInfo = new FileInfo(relativePath);
        Path path = Paths.get(repoRoot, fileInfo.getPath());

        try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
            long fileSize = Files.size(path);
            fileInfo.setFileSize(fileSize);
            if (!ignoreFileSizeLimit && fileSize > fileSizeLimit) {
                fileInfo.setExceedsSizeLimit(true);
                if (skipIgnoredFileAnalysis) {
                    logger.log(Level.WARNING, String.format(MESSAGE_FILE_SIZE_LIMIT_EXCEEDED,
                            fileInfo.getPath(), fileSize, fileSizeLimit, MESSAGE_FILE_ANALYSIS_SKIPPED));
                    fileInfo.setFileAnalyzed(false);
                    return fileInfo;
                }
                logger.log(Level.WARNING, String.format(MESSAGE_FILE_SIZE_LIMIT_EXCEEDED,
                        fileInfo.getPath(), fileSize, fileSizeLimit, MESSAGE_FILE_EXCLUDED_FROM_REPORT));
            }
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
     *
     * @throws AssertionError if matching line number pattern in chunk header fails.
     */
    private int getStartingLineNumber(String linesChanged) throws AssertionError {
        Matcher chunkHeaderMatcher = STARTING_LINE_NUMBER_PATTERN.matcher(linesChanged);

        if (!chunkHeaderMatcher.find()) {
            logger.severe(String.format(MATCH_GROUP_FAIL_MESSAGE_FORMAT, "line changed", linesChanged));
            throw new AssertionError("Should not have error matching line number pattern inside chunk header!");
        }

        return Integer.parseInt(chunkHeaderMatcher.group(STARTING_LINE_NUMBER_GROUP_NAME));
    }

    /**
     * Returns true if {@code filePath} is valid and the file is not in binary (i.e. part of {@code textFilesSet}).
     */
    private boolean isValidTextFile(String filePath, Set<Path> textFilesSet) {
        boolean isValidFilePath;
        try {
            isValidFilePath = FileUtil.isValidPathWithLogging(filePath);
        } catch (InvalidPathException ipe) {
            logger.log(Level.WARNING, String.format(MESSAGE_INVALID_FILE_PATH, filePath));
            isValidFilePath = false;
        }

        return isValidFilePath && textFilesSet.contains(Paths.get(filePath));
    }
}
