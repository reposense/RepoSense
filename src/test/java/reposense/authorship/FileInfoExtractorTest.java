package reposense.authorship;

import static reposense.model.RepoConfiguration.DEFAULT_FILE_SIZE_LIMIT;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import reposense.authorship.model.FileInfo;
import reposense.git.GitCheckout;
import reposense.model.Author;
import reposense.model.FileTypeTest;
import reposense.model.RepoConfiguration;
import reposense.template.GitTestTemplate;
import reposense.util.TestUtil;

public class FileInfoExtractorTest extends GitTestTemplate {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "resources", "FileInfoExtractorTest");
    private static final Path FILE_WITH_SPECIAL_CHARACTER = TEST_DATA_FOLDER.resolve("fileWithSpecialCharacters.txt");
    private static final Path FILE_WITHOUT_SPECIAL_CHARACTER = TEST_DATA_FOLDER
            .resolve("fileWithoutSpecialCharacters.txt");

    private static final String EDITED_FILE_INFO_BRANCH = "getEditedFileInfos-test";
    private static final String DIRECTORY_WITH_VALID_WHITELISTED_NAME_BRANCH = "directory-with-valid-whitelisted-name";
    private static final String BRANCH_WITH_VALID_WHITELISTED_FILE_NAME_BRANCH =
            "535-FileInfoExtractorTest-branchWithValidWhitelistedFileName.txt";
    private static final String BRANCH_WITH_BINARY_FILES =
            "728-FileInfoExtractorTest-getNonBinaryFilesList_directoryWithBinaryFiles_success";
    private static final String BRANCH_WITH_RARE_FILE_FORMATS =
            "708-FileInfoExtractorTest-extractFileInfos_withoutSpecifiedFormats_success";
    private static final String BRANCH_WITH_LARGE_FILE =
            "1647-FileAnalyzerTest-analyzeTextFile_fileExceedingFileSizeLimit_success";
    private static final String FILE_WITH_LARGE_SIZE = "largeFile.json";

    private static final String FEBRUARY_EIGHT_COMMIT_HASH = "768015345e70f06add2a8b7d1f901dc07bf70582";

    private RepoConfiguration config;
    private FileInfoExtractor fileInfoExtractor = new FileInfoExtractor();

    @BeforeEach
    public void before() throws Exception {
        super.before();
        config = configs.get();
    }

    @Test
    public void extractFileInfosTest() {
        config.addAuthorNamesToAuthorMapEntry(new Author(MAIN_AUTHOR_NAME), MAIN_AUTHOR_NAME);
        config.addAuthorNamesToAuthorMapEntry(new Author(FAKE_AUTHOR_NAME), FAKE_AUTHOR_NAME);

        GitCheckout.checkout(config.getRepoRoot(), TEST_COMMIT_HASH);
        List<FileInfo> files = fileInfoExtractor.extractTextFileInfos(config);

        Assertions.assertEquals(6, files.size());

        Assertions.assertTrue(isFileExistence(Paths.get("README.md"), files));
        Assertions.assertTrue(isFileExistence(Paths.get("annotationTest.java"), files));
        Assertions.assertTrue(isFileExistence(Paths.get("blameTest.java"), files));
        Assertions.assertTrue(isFileExistence(Paths.get("newPos/movedFile.java"), files));
        Assertions.assertTrue(isFileExistence(Paths.get("inMasterBranch.java"), files));
        Assertions.assertTrue(isFileExistence(Paths.get("newFile.java"), files));
    }

    @Test
    public void extractFileInfos_skipGlobs_success() {
        config.addAuthorNamesToAuthorMapEntry(new Author(MAIN_AUTHOR_NAME), MAIN_AUTHOR_NAME);
        config.addAuthorNamesToAuthorMapEntry(new Author(FAKE_AUTHOR_NAME), FAKE_AUTHOR_NAME);
        config.setIgnoreGlobList(Arrays.asList("newPos/**.java", "**.md"));

        GitCheckout.checkout(config.getRepoRoot(), TEST_COMMIT_HASH);
        List<FileInfo> files = fileInfoExtractor.extractTextFileInfos(config);

        Assertions.assertEquals(4, files.size());

        Assertions.assertFalse(isFileExistence(Paths.get("README.md"), files));
        Assertions.assertTrue(isFileExistence(Paths.get("annotationTest.java"), files));
        Assertions.assertTrue(isFileExistence(Paths.get("blameTest.java"), files));
        Assertions.assertFalse(isFileExistence(Paths.get("newPos/movedFile.java"), files));
        Assertions.assertTrue(isFileExistence(Paths.get("inMasterBranch.java"), files));
        Assertions.assertTrue(isFileExistence(Paths.get("newFile.java"), files));
    }

    @Test
    public void extractFileInfos_sinceDateFebrauaryNineToLatestCommit_success() {
        LocalDateTime date = TestUtil.getSinceDate(2018, Month.FEBRUARY.getValue(), 9);
        config.setSinceDate(date);

        List<FileInfo> files = fileInfoExtractor.extractTextFileInfos(config);
        Assertions.assertEquals(5, files.size());

        // files edited within commit range
        Assertions.assertTrue(isFileExistence(Paths.get("README.md"), files));
        Assertions.assertTrue(isFileExistence(Paths.get("newPos/movedFile.java"), files));
        Assertions.assertTrue(isFileExistence(Paths.get("annotationTest.java"), files));

        // files not edited within commit range
        Assertions.assertFalse(isFileExistence(Paths.get("inMasterBranch.java"), files));
        Assertions.assertFalse(isFileExistence(Paths.get("blameTest.java"), files));
        Assertions.assertFalse(isFileExistence(Paths.get("newFile.java"), files));
    }

    @Test
    public void extractFileInfos_directoryWithValidWhitelistedName_success() {
        GitCheckout.checkout(config.getRepoRoot(), DIRECTORY_WITH_VALID_WHITELISTED_NAME_BRANCH);
        List<FileInfo> files = fileInfoExtractor.extractTextFileInfos(config);

        Assertions.assertEquals(7, files.size());
        Assertions.assertTrue(isFileExistence(Paths.get(".gradle/anything.txt"), files));
    }

    @Test
    public void extractFileInfos_branchWithValidWhitelistedFileName_success() {
        GitCheckout.checkout(config.getRepoRoot(), BRANCH_WITH_VALID_WHITELISTED_FILE_NAME_BRANCH);
        List<FileInfo> files = fileInfoExtractor.extractTextFileInfos(config);

        Assertions.assertTrue(isFileExistence(Paths.get("whitelisted-format.txt"), files));
    }

    @Test
    public void extractFileInfos_sinceDateAfterLatestCommit_emptyResult() {
        LocalDateTime date = TestUtil.getSinceDate(2050, Month.DECEMBER.getValue(), 31);
        config.setSinceDate(date);

        List<FileInfo> files = fileInfoExtractor.extractTextFileInfos(config);
        Assertions.assertTrue(files.isEmpty());
    }

    @Test
    public void extractFileInfos_untilDateBeforeFirstCommit_emptyResult() {
        LocalDateTime date = TestUtil.getUntilDate(2015, Month.DECEMBER.getValue(), 31);
        config.setUntilDate(date);

        List<FileInfo> files = fileInfoExtractor.extractTextFileInfos(config);
        Assertions.assertTrue(files.isEmpty());
    }

    @Test
    public void getEditedFileInfos_editFileInfoBranchSinceFebrauryEight_success() {
        GitCheckout.checkout(config.getRepoRoot(), EDITED_FILE_INFO_BRANCH);
        List<FileInfo> files = fileInfoExtractor.getEditedFileInfos(config, FEBRUARY_EIGHT_COMMIT_HASH);

        Assertions.assertEquals(3, files.size());
        Assertions.assertTrue(isFileExistence(Paths.get("README.md"), files));
        Assertions.assertTrue(isFileExistence(Paths.get("annotationTest.java"), files));
        Assertions.assertTrue(isFileExistence(Paths.get("newPos/movedFile.java"), files));

        // file renamed without changing content, not included
        Assertions.assertFalse(isFileExistence(Paths.get("renamedFile.java"), files));
    }

    @Test
    public void getEditedFileInfos_editFileInfoBranchSinceFebrauryEightSkipGlobs_success() {
        GitCheckout.checkout(config.getRepoRoot(), EDITED_FILE_INFO_BRANCH);
        config.setIgnoreGlobList(Arrays.asList("**.md"));

        List<FileInfo> files = fileInfoExtractor.getEditedFileInfos(config, FEBRUARY_EIGHT_COMMIT_HASH);

        Assertions.assertEquals(2, files.size());

        Assertions.assertFalse(isFileExistence(Paths.get("README.md"), files));
        Assertions.assertTrue(isFileExistence(Paths.get("annotationTest.java"), files));
        Assertions.assertTrue(isFileExistence(Paths.get("newPos/movedFile.java"), files));

        // file renamed without changing content, not included
        Assertions.assertFalse(isFileExistence(Paths.get("renamedFile.java"), files));
    }

    @Test
    public void getEditedFileInfos_editFileInfoBranchSinceFirstCommit_success() {
        GitCheckout.checkout(config.getRepoRoot(), EDITED_FILE_INFO_BRANCH);
        List<FileInfo> files = fileInfoExtractor.getEditedFileInfos(config, FIRST_COMMIT_HASH);

        Assertions.assertEquals(5, files.size());

        // empty file created, not included
        Assertions.assertFalse(isFileExistence(Paths.get("inMasterBranch.java"), files));
    }

    @Test
    public void generateFileInfo_fileWithSpecialCharacters_correctFileInfoGenerated() {
        FileInfo fileInfo = fileInfoExtractor.generateFileInfo(".", FILE_WITH_SPECIAL_CHARACTER.toString(),
                DEFAULT_FILE_SIZE_LIMIT, false, false);
        Assertions.assertEquals(5, fileInfo.getLines().size());
    }

    @Test
    public void generateFileInfo_fileWithoutSpecialCharacters_correctFileInfoGenerated() {
        FileInfo fileInfo = fileInfoExtractor.generateFileInfo(".", FILE_WITHOUT_SPECIAL_CHARACTER.toString(),
                DEFAULT_FILE_SIZE_LIMIT, false, false);
        Assertions.assertEquals(5, fileInfo.getLines().size());
    }

    @Test
    public void generateFileInfo_fileExceedingSizeLimit_correctFileInfoGenerated() {
        config.setBranch(BRANCH_WITH_LARGE_FILE);
        GitCheckout.checkout(config.getRepoRoot(), config.getBranch());
        FileInfo fileInfo = fileInfoExtractor.generateFileInfo(config.getRepoRoot(), FILE_WITH_LARGE_SIZE,
                DEFAULT_FILE_SIZE_LIMIT, false, false);

        Assertions.assertTrue(fileInfo.isFileAnalyzed());
        Assertions.assertEquals(46902, fileInfo.getLines().size());
        Assertions.assertEquals(fileInfo.getFileSize() > DEFAULT_FILE_SIZE_LIMIT, fileInfo.exceedsFileLimit());
    }

    @Test
    public void generateFileInfo_fileExceedingSizeLimitAndSkipped_correctFileInfoGenerated() {
        config.setBranch(BRANCH_WITH_LARGE_FILE);
        GitCheckout.checkout(config.getRepoRoot(), config.getBranch());
        FileInfo fileInfo = fileInfoExtractor.generateFileInfo(config.getRepoRoot(), FILE_WITH_LARGE_SIZE,
                DEFAULT_FILE_SIZE_LIMIT, false, true);

        Assertions.assertFalse(fileInfo.isFileAnalyzed());
        Assertions.assertEquals(0, fileInfo.getLines().size());
        Assertions.assertEquals(fileInfo.getFileSize() > DEFAULT_FILE_SIZE_LIMIT, fileInfo.exceedsFileLimit());
    }

    @Test
    public void generateFileInfo_fileExceedingSizeLimitAndLimitIgnored_correctFileInfoGenerated() {
        config.setBranch(BRANCH_WITH_LARGE_FILE);
        GitCheckout.checkout(config.getRepoRoot(), config.getBranch());
        FileInfo fileInfo = fileInfoExtractor.generateFileInfo(config.getRepoRoot(), FILE_WITH_LARGE_SIZE,
                DEFAULT_FILE_SIZE_LIMIT, true, false);

        Assertions.assertTrue(fileInfo.isFileAnalyzed());
        Assertions.assertEquals(46902, fileInfo.getLines().size());
        Assertions.assertFalse(fileInfo.exceedsFileLimit());
    }

    @Test
    public void getFilesList_getTextFilesFromRepoWithBinaryFiles_success() {
        List<String> textFilesList = Arrays.asList(
                "binaryFileTest/nonBinaryFile.txt", "My Documents/wordToHtml.htm", "My Pictures/notPngPicture.png",
                "My Documents/wordToHtml_files/colorschememapping.xml", "My Documents/wordToHtml_files/filelist.xml",
                "My Documents/notPdfDocument.pdf");
        List<String> binaryFilesList = Arrays.asList(
                "binaryFileTest/binaryFile.txt", "My Documents/word.docx", "My Documents/pdfDocument.pdf",
                "My Documents/wordToHtml_files/themedata.thmx", "My Pictures/pngPicture.png");
        GitCheckout.checkoutBranch(config.getRepoRoot(), BRANCH_WITH_BINARY_FILES);
        Set<Path> textFiles = fileInfoExtractor.getFiles(config, false);

        Assertions.assertEquals(6, textFiles.size());
        // Non binary files should be captured
        textFilesList.forEach(textFile -> Assertions.assertTrue(textFiles.contains(Paths.get(textFile))));
        // Binary files should be ignored
        binaryFilesList.forEach(binFile -> Assertions.assertFalse(textFiles.contains(Paths.get(binFile))));
    }

    @Test
    public void getFilesList_getBinaryFilesFromRepoWithTextFiles_success() {
        List<String> textFilesList = Arrays.asList(
                "binaryFileTest/nonBinaryFile.txt", "My Documents/wordToHtml.htm", "My Pictures/notPngPicture.png",
                "My Documents/wordToHtml_files/colorschememapping.xml", "My Documents/wordToHtml_files/filelist.xml",
                "My Documents/notPdfDocument.pdf");
        List<String> binaryFilesList = Arrays.asList(
                "binaryFileTest/binaryFile.txt", "My Documents/word.docx", "My Documents/pdfDocument.pdf",
                "My Documents/wordToHtml_files/themedata.thmx", "My Pictures/pngPicture.png");
        GitCheckout.checkoutBranch(config.getRepoRoot(), BRANCH_WITH_BINARY_FILES);
        Set<Path> binaryFiles = fileInfoExtractor.getFiles(config, true);

        Assertions.assertEquals(5, binaryFiles.size());
        // Binary files should be captured
        binaryFilesList.forEach(binFile -> Assertions.assertTrue(binaryFiles.contains(Paths.get(binFile))));
        // Non binary files should be ignored
        textFilesList.forEach(textFile -> Assertions.assertFalse(binaryFiles.contains(Paths.get(textFile))));
    }

    @Test
    public void extractFileInfos_withoutSpecifiedFormats_success() {
        List<String> textFilesList = Arrays.asList(
                "binaryFileTest/nonBinaryFile.ARBIFORMAT", "My Documents/wordToHtml.htm",
                "My Pictures/notPngPicture.png", "My Documents/wordToHtml_files/colorschememapping.xml",
                "My Documents/wordToHtml_files/filelist.xml", "My Documents/notPdfDocument.fdp");
        List<String> binaryFilesList = Arrays.asList(
                "binaryFileTest/binaryFile.ARBIFORMAT", "My Documents/word.docx", "My Documents/pdfDocument.fdp",
                "My Documents/wordToHtml_files/themedata.thmx", "My Pictures/pngPicture.png");
        config.setFormats(FileTypeTest.NO_SPECIFIED_FORMATS);
        GitCheckout.checkoutBranch(config.getRepoRoot(), BRANCH_WITH_RARE_FILE_FORMATS);

        List<FileInfo> files = fileInfoExtractor.extractTextFileInfos(config);

        Assertions.assertEquals(textFilesList.size(), files.size());
        // Non binary files should be captured
        textFilesList.forEach(textFile -> Assertions.assertTrue(isFileExistence(Paths.get(textFile), files)));
        // Binary files should be ignored
        binaryFilesList.forEach(binFile -> Assertions.assertFalse(isFileExistence(Paths.get(binFile), files)));
    }

    @Test
    public void getEditedFileInfos_repoWithFilesWithSpaces_success() {
        List<FileInfo> fileInfos = fileInfoExtractor.getEditedFileInfos(config, FEBRUARY_EIGHT_COMMIT_HASH);

        Assertions.assertTrue(isFileExistence(Paths.get("space test.txt"), fileInfos));
    }

    private boolean isFileExistence(Path filePath, List<FileInfo> files) {
        return files.stream().anyMatch(file -> Paths.get(file.getPath()).equals(filePath));
    }
}
