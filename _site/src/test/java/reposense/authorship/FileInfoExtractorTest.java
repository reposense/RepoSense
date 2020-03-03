package reposense.authorship;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import reposense.authorship.model.FileInfo;
import reposense.git.GitCheckout;
import reposense.model.Author;
import reposense.model.FileTypeTest;
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
    private static final String FEBRUARY_EIGHT_COMMIT_HASH = "768015345e70f06add2a8b7d1f901dc07bf70582";

    @Test
    public void extractFileInfosTest() {
        config.getAuthorEmailsAndAliasesMap().put(MAIN_AUTHOR_NAME, new Author(MAIN_AUTHOR_NAME));
        config.getAuthorEmailsAndAliasesMap().put(FAKE_AUTHOR_NAME, new Author(FAKE_AUTHOR_NAME));
        GitCheckout.checkout(config.getRepoRoot(), TEST_COMMIT_HASH);
        List<FileInfo> files = FileInfoExtractor.extractFileInfos(config);
        Assert.assertEquals(6, files.size());
        Assert.assertTrue(isFileExistence(Paths.get("README.md"), files));
        Assert.assertTrue(isFileExistence(Paths.get("annotationTest.java"), files));
        Assert.assertTrue(isFileExistence(Paths.get("blameTest.java"), files));
        Assert.assertTrue(isFileExistence(Paths.get("newPos/movedFile.java"), files));
        Assert.assertTrue(isFileExistence(Paths.get("inMasterBranch.java"), files));
        Assert.assertTrue(isFileExistence(Paths.get("newFile.java"), files));
    }

    @Test
    public void extractFileInfos_sinceDateFebrauaryNineToLatestCommit_success() {
        Date date = TestUtil.getSinceDate(2018, Calendar.FEBRUARY, 9);
        config.setSinceDate(date);

        List<FileInfo> files = FileInfoExtractor.extractFileInfos(config);
        Assert.assertEquals(4, files.size());

        // files edited within commit range
        Assert.assertTrue(isFileExistence(Paths.get("README.md"), files));
        Assert.assertTrue(isFileExistence(Paths.get("newPos/movedFile.java"), files));
        Assert.assertTrue(isFileExistence(Paths.get("annotationTest.java"), files));

        // files not edited within commit range
        Assert.assertFalse(isFileExistence(Paths.get("inMasterBranch.java"), files));
        Assert.assertFalse(isFileExistence(Paths.get("blameTest.java"), files));
        Assert.assertFalse(isFileExistence(Paths.get("newFile.java"), files));
    }

    @Test
    public void extractFileInfos_directoryWithValidWhitelistedName_success() {
        GitCheckout.checkout(config.getRepoRoot(), DIRECTORY_WITH_VALID_WHITELISTED_NAME_BRANCH);
        List<FileInfo> files = FileInfoExtractor.extractFileInfos(config);

        Assert.assertEquals(7, files.size());
        Assert.assertTrue(isFileExistence(Paths.get(".gradle/anything.txt"), files));
    }

    @Test
    public void extractFileInfos_branchWithValidWhitelistedFileName_success() {
        GitCheckout.checkout(config.getRepoRoot(), BRANCH_WITH_VALID_WHITELISTED_FILE_NAME_BRANCH);
        List<FileInfo> files = FileInfoExtractor.extractFileInfos(config);

        Assert.assertTrue(isFileExistence(Paths.get("whitelisted-format.txt"), files));
    }

    @Test
    public void extractFileInfos_sinceDateAfterLatestCommit_emptyResult() {
        Date date = TestUtil.getSinceDate(2050, 12, 31);
        config.setSinceDate(date);

        List<FileInfo> files = FileInfoExtractor.extractFileInfos(config);
        Assert.assertTrue(files.isEmpty());
    }

    @Test
    public void extractFileInfos_untilDateBeforeFirstCommit_emptyResult() {
        Date date = TestUtil.getUntilDate(2015, 12, 31);
        config.setUntilDate(date);

        List<FileInfo> files = FileInfoExtractor.extractFileInfos(config);
        Assert.assertTrue(files.isEmpty());
    }

    @Test
    public void getEditedFileInfos_editFileInfoBranchSinceFebrauryEight_success() {
        GitCheckout.checkout(config.getRepoRoot(), EDITED_FILE_INFO_BRANCH);
        List<FileInfo> files = FileInfoExtractor.getEditedFileInfos(config, FEBRUARY_EIGHT_COMMIT_HASH);

        Assert.assertEquals(3, files.size());
        Assert.assertTrue(isFileExistence(Paths.get("README.md"), files));
        Assert.assertTrue(isFileExistence(Paths.get("annotationTest.java"), files));
        Assert.assertTrue(isFileExistence(Paths.get("newPos/movedFile.java"), files));

        // file renamed without changing content, not included
        Assert.assertFalse(isFileExistence(Paths.get("renamedFile.java"), files));
    }

    @Test
    public void getEditedFileInfos_editFileInfoBranchSinceFirstCommit_success() {
        GitCheckout.checkout(config.getRepoRoot(), EDITED_FILE_INFO_BRANCH);
        List<FileInfo> files = FileInfoExtractor.getEditedFileInfos(config, FIRST_COMMIT_HASH);

        Assert.assertEquals(5, files.size());

        // empty file created, not included
        Assert.assertFalse(isFileExistence(Paths.get("inMasterBranch.java"), files));
    }

    @Test
    public void generateFileInfo_fileWithSpecialCharacters_correctFileInfoGenerated() {
        FileInfo fileInfo = FileInfoExtractor.generateFileInfo(".", FILE_WITH_SPECIAL_CHARACTER.toString());
        Assert.assertEquals(5, fileInfo.getLines().size());
    }

    @Test
    public void generateFileInfo_fileWithoutSpecialCharacters_correctFileInfoGenerated() {
        FileInfo fileInfo = FileInfoExtractor.generateFileInfo(".", FILE_WITHOUT_SPECIAL_CHARACTER.toString());
        Assert.assertEquals(5, fileInfo.getLines().size());
    }

    @Test
    public void getNonBinaryFilesList_directoryWithBinaryFiles_success() {
        List<String> nonBinaryFilesList = Arrays.asList(
                "binaryFileTest/nonBinaryFile.txt", "My Documents/wordToHtml.htm", "My Pictures/notPngPicture.png",
                "My Documents/wordToHtml_files/colorschememapping.xml", "My Documents/wordToHtml_files/filelist.xml",
                "My Documents/notPdfDocument.pdf");
        List<String> binaryFilesList = Arrays.asList(
                "binaryFileTest/binaryFile.txt", "My Documents/word.docx", "My Documents/pdfDocument.pdf",
                "My Documents/wordToHtml_files/themedata.thmx", "My Pictures/pngPicture.png");
        GitCheckout.checkoutBranch(config.getRepoRoot(), BRANCH_WITH_BINARY_FILES);
        Set<Path> files = FileInfoExtractor.getNonBinaryFilesList(config);

        Assert.assertEquals(6, files.size());
        // Non binary files should be captured
        nonBinaryFilesList.forEach(nonBinFile -> Assert.assertTrue(files.contains(Paths.get(nonBinFile))));
        // Binary files should be ignored
        binaryFilesList.forEach(binFile -> Assert.assertFalse(files.contains(Paths.get(binFile))));
    }

    @Test
    public void extractFileInfos_withoutSpecifiedFormats_success() {
        List<String> nonBinaryFilesList = Arrays.asList(
                "binaryFileTest/nonBinaryFile.ARBIFORMAT", "My Documents/wordToHtml.htm",
                "My Pictures/notPngPicture.png", "My Documents/wordToHtml_files/colorschememapping.xml",
                "My Documents/wordToHtml_files/filelist.xml", "My Documents/notPdfDocument.fdp");
        List<String> binaryFilesList = Arrays.asList(
                "binaryFileTest/binaryFile.ARBIFORMAT", "My Documents/word.docx", "My Documents/pdfDocument.fdp",
                "My Documents/wordToHtml_files/themedata.thmx", "My Pictures/pngPicture.png");
        config.setFormats(FileTypeTest.NO_SPECIFIED_FORMATS);
        GitCheckout.checkoutBranch(config.getRepoRoot(), BRANCH_WITH_RARE_FILE_FORMATS);

        List<FileInfo> files = FileInfoExtractor.extractFileInfos(config);

        Assert.assertEquals(nonBinaryFilesList.size(), files.size());
        // Non binary files should be captured
        nonBinaryFilesList.forEach(nonBinFile -> Assert.assertTrue(isFileExistence(Paths.get(nonBinFile), files)));
        // Binary files should be ignored
        binaryFilesList.forEach(binFile -> Assert.assertFalse(isFileExistence(Paths.get(binFile), files)));
    }

    private boolean isFileExistence(Path filePath, List<FileInfo> files) {
        return files.stream().anyMatch(file -> Paths.get(file.getPath()).equals(filePath));
    }
}
