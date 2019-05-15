package reposense.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import reposense.report.SummaryReportJson;

public class FileUtilTest {

    private static final Path FILE_UTIL_TEST_DIRECTORY = new File(FileUtilTest.class.getClassLoader()
            .getResource("FileUtilTest").getFile()).toPath().toAbsolutePath();
    private static final Path RELEVANT_REPO_DIRECTORY_PATH = Paths.get(FILE_UTIL_TEST_DIRECTORY.toString(),
            "reposense-report-test");
    private static final Path ARCHIVE_ZIP_PATH = Paths.get(FILE_UTIL_TEST_DIRECTORY.toString(),
            FileUtil.ZIP_FILE);
    private static final Path EXPECTED_UNZIPPED_DIRECTORY_PATH = Paths.get(FILE_UTIL_TEST_DIRECTORY.toString(),
            "expectedUnzip");
    private static final Path TEST_ZIP_PATH = Paths.get(FILE_UTIL_TEST_DIRECTORY.toString(), "testZip.zip");
    private static final Path UNZIPPED_DIRECTORY_PATH = Paths.get(FILE_UTIL_TEST_DIRECTORY.toString(),
            "UnzippedFolder");
    private static final Path[] REPO_FOLDERS = {Paths.get("reposense_testrepo-Beta"),
            Paths.get("reposense_testrepo-Charlie")};
    private static final Path EXPECTED_RELEVANT_FOLDER_PATH = Paths.get(FILE_UTIL_TEST_DIRECTORY.toString(),
            "expectedRelevantUnzippedFiles");

    /**
     * Ensures that only the specified files and folders with the corresponding file types get zipped.
     */
    @Test
    public void zip_onlyRelevantFiles_success() throws IOException {
        HashSet<Path> relevantFolders = new HashSet<>(Arrays.asList(REPO_FOLDERS));
        HashSet<Path> relevantFiles = new HashSet<>();
        relevantFolders.add(Paths.get(RELEVANT_REPO_DIRECTORY_PATH + File.separator
                        + "reposense_testrepo-Beta"));
        relevantFolders.add(Paths.get(RELEVANT_REPO_DIRECTORY_PATH + File.separator
                + "reposense_testrepo-Charlie"));
        relevantFiles.add(Paths.get(SummaryReportJson.SUMMARY_JSON_FILE_NAME));

        FileUtil.zipRelativeFiles(relevantFolders, relevantFiles,
                RELEVANT_REPO_DIRECTORY_PATH, FILE_UTIL_TEST_DIRECTORY, ".json");
        FileUtil.unzip(ARCHIVE_ZIP_PATH, UNZIPPED_DIRECTORY_PATH);

        Assert.assertTrue(TestUtil.compareDirectories(UNZIPPED_DIRECTORY_PATH, EXPECTED_RELEVANT_FOLDER_PATH));
    }

    @Test
    public void zip_validLocation_success() throws IOException {
        FileUtil.zip(FILE_UTIL_TEST_DIRECTORY, ".json");
        Assert.assertTrue(Files.exists(ARCHIVE_ZIP_PATH));
        Assert.assertTrue(Files.size(ARCHIVE_ZIP_PATH) > 0);
    }

    @Test
    public void zip_validFileType_success() throws IOException {
        FileUtil.zip (FILE_UTIL_TEST_DIRECTORY, ".csv");
        Assert.assertTrue(Files.exists(ARCHIVE_ZIP_PATH));
        Assert.assertTrue(Files.size(ARCHIVE_ZIP_PATH) > 0);
    }

    @Test
    public void unzip_validZipFile_success() throws IOException {
        FileUtil.unzip(TEST_ZIP_PATH, UNZIPPED_DIRECTORY_PATH);
        Assert.assertTrue(Files.exists(UNZIPPED_DIRECTORY_PATH));
        Assert.assertTrue(TestUtil.compareDirectories(UNZIPPED_DIRECTORY_PATH, EXPECTED_UNZIPPED_DIRECTORY_PATH));
    }

    @Test
    public void unzip_invalidZipFile_fail() throws IOException {
        Path invalidZipFile = Paths.get(FILE_UTIL_TEST_DIRECTORY.toString(), "test.csv");
        FileUtil.unzip(invalidZipFile, FILE_UTIL_TEST_DIRECTORY);
        Assert.assertFalse(Files.exists(Paths.get(FILE_UTIL_TEST_DIRECTORY.toString(), "test")));
    }

    @After
    public void after() throws IOException, NullPointerException {
        Files.deleteIfExists(ARCHIVE_ZIP_PATH);
        if (Files.exists(UNZIPPED_DIRECTORY_PATH)) {
            FileUtil.deleteDirectory(UNZIPPED_DIRECTORY_PATH.toString());
        }
    }
}
