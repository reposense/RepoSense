package reposense.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import reposense.report.SummaryJson;

public class FileUtilTest {

    private static final Path FILE_UTIL_TEST_DIRECTORY = new File(FileUtilTest.class.getClassLoader()
            .getResource("FileUtilTest").getFile()).toPath().toAbsolutePath();
    private static final Path REPO_REPORT_DIRECTORY_PATH = Paths.get(FILE_UTIL_TEST_DIRECTORY.toString(),
            "reposense-report-test");
    private static final Path ARCHIVE_ZIP_PATH = Paths.get(FILE_UTIL_TEST_DIRECTORY.toString(), FileUtil.ZIP_FILE);
    private static final Path EXPECTED_UNZIPPED_DIRECTORY_PATH = Paths.get(FILE_UTIL_TEST_DIRECTORY.toString(),
            "expectedUnzip");
    private static final Path TEST_ZIP_PATH = Paths.get(FILE_UTIL_TEST_DIRECTORY.toString(), "testZip.zip");
    private static final Path UNZIPPED_DIRECTORY_PATH = Paths.get(FILE_UTIL_TEST_DIRECTORY.toString(),
            "UnzippedFolder");
    private static final List<Path> REPORT_FOLDER_FILE_PATHS = Arrays.asList(
            Paths.get(REPO_REPORT_DIRECTORY_PATH.toString(), "reposense_testrepo-Beta").toAbsolutePath(),
            Paths.get(REPO_REPORT_DIRECTORY_PATH.toString(), "reposense_testrepo-Charlie").toAbsolutePath(),
            Paths.get(REPO_REPORT_DIRECTORY_PATH.toString(),
            SummaryJson.SUMMARY_JSON_FILE_NAME).toAbsolutePath()
    );
    private static final Path EXPECTED_RELEVANT_FOLDER_PATH = Paths.get(FILE_UTIL_TEST_DIRECTORY.toString(),
            "expectedRelevantUnzippedFiles");

    /**
     * Ensures that only the specified files and folders with the corresponding file types get zipped.
     */
    @Test
    public void zipFoldersAndFiles_onlyRelevantFiles_success() throws Exception {
        FileUtil.zipFoldersAndFiles(REPORT_FOLDER_FILE_PATHS, REPO_REPORT_DIRECTORY_PATH, FILE_UTIL_TEST_DIRECTORY,
                ".json");
        FileUtil.unzip(ARCHIVE_ZIP_PATH, UNZIPPED_DIRECTORY_PATH);
        Assert.assertTrue(TestUtil.compareDirectories(EXPECTED_RELEVANT_FOLDER_PATH, UNZIPPED_DIRECTORY_PATH));
    }

    @Test
    public void zipFoldersAndFiles_validLocation_success() throws Exception {
        FileUtil.zipFoldersAndFiles(REPORT_FOLDER_FILE_PATHS, REPO_REPORT_DIRECTORY_PATH, FILE_UTIL_TEST_DIRECTORY,
                ".json");
        Assert.assertTrue(Files.exists(ARCHIVE_ZIP_PATH));
        Assert.assertTrue(Files.size(ARCHIVE_ZIP_PATH) > 0);
    }

    @Test
    public void zipFoldersAndFiles_validFileType_success() throws Exception {
        List<Path> paths = Collections.singletonList(Paths.get(FILE_UTIL_TEST_DIRECTORY.toString(), "test.csv"));
        FileUtil.zipFoldersAndFiles(paths, FILE_UTIL_TEST_DIRECTORY, ".csv");
        Assert.assertTrue(Files.exists(ARCHIVE_ZIP_PATH));
        Assert.assertTrue(Files.size(ARCHIVE_ZIP_PATH) > 0);
    }

    @Test
    public void unzip_validZipFile_success() throws Exception {
        FileUtil.unzip(TEST_ZIP_PATH, UNZIPPED_DIRECTORY_PATH);
        Assert.assertTrue(Files.exists(UNZIPPED_DIRECTORY_PATH));
        Assert.assertTrue(TestUtil.compareDirectories(EXPECTED_UNZIPPED_DIRECTORY_PATH, UNZIPPED_DIRECTORY_PATH));
    }

    @Test
    public void unzip_invalidZipFile_fail() throws Exception {
        Path invalidZipFile = Paths.get(FILE_UTIL_TEST_DIRECTORY.toString(), "test.csv");
        FileUtil.unzip(invalidZipFile, FILE_UTIL_TEST_DIRECTORY);
        Assert.assertFalse(Files.exists(Paths.get(FILE_UTIL_TEST_DIRECTORY.toString(), "test")));
    }

    @After
    public void after() throws Exception {
        Files.deleteIfExists(ARCHIVE_ZIP_PATH);
        if (Files.exists(UNZIPPED_DIRECTORY_PATH)) {
            FileUtil.deleteDirectory(UNZIPPED_DIRECTORY_PATH.toString());
        }
    }
}
