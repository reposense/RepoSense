package reposense.util;

import static reposense.util.TestUtil.loadResource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import reposense.report.SummaryJson;

public class FileUtilTest {

    private static final Path FILE_UTIL_TEST_DIRECTORY = loadResource(FileUtilTest.class, "FileUtilTest");
    private static final Path REPO_REPORT_DIRECTORY_PATH = Paths.get(FILE_UTIL_TEST_DIRECTORY.toString(),
            "reposense-report-test");
    private static final Path ARCHIVE_ZIP_PATH = Paths.get(FILE_UTIL_TEST_DIRECTORY.toString(), FileUtil.ZIP_FILE);
    private static final Path EXPECTED_UNZIPPED_DIRECTORY_PATH = Paths.get(FILE_UTIL_TEST_DIRECTORY.toString(),
            "expectedUnzip");
    private static final String TEST_ZIP_FILE_NAME = "testZip.zip";
    private static final Path TEST_ZIP_PATH = Paths.get(FILE_UTIL_TEST_DIRECTORY.toString(), TEST_ZIP_FILE_NAME);
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
    private static final String TEST_FILE_NAME = "/filename.txt";
    private static final Path TEST_FILE_PATH = Paths.get(FILE_UTIL_TEST_DIRECTORY.toString(), TEST_FILE_NAME);

    /**
     * Ensures that only the specified files and folders with the corresponding file types get zipped.
     */
    @Test
    public void zipFoldersAndFiles_onlyRelevantFiles_success() throws Exception {
        FileUtil.zipFoldersAndFiles(REPORT_FOLDER_FILE_PATHS, REPO_REPORT_DIRECTORY_PATH, FILE_UTIL_TEST_DIRECTORY,
                ".json");
        FileUtil.unzip(ARCHIVE_ZIP_PATH, UNZIPPED_DIRECTORY_PATH);
        Assertions.assertTrue(TestUtil.compareDirectories(EXPECTED_RELEVANT_FOLDER_PATH, UNZIPPED_DIRECTORY_PATH));
    }

    @Test
    public void zipFoldersAndFiles_validLocation_success() throws Exception {
        FileUtil.zipFoldersAndFiles(REPORT_FOLDER_FILE_PATHS, REPO_REPORT_DIRECTORY_PATH, FILE_UTIL_TEST_DIRECTORY,
                ".json");
        Assertions.assertTrue(Files.exists(ARCHIVE_ZIP_PATH));
        Assertions.assertTrue(Files.size(ARCHIVE_ZIP_PATH) > 0);
    }

    @Test
    public void zipFoldersAndFiles_validFileType_success() throws Exception {
        List<Path> paths = Collections.singletonList(Paths.get(FILE_UTIL_TEST_DIRECTORY.toString(), "test.csv"));
        FileUtil.zipFoldersAndFiles(paths, FILE_UTIL_TEST_DIRECTORY, ".csv");
        Assertions.assertTrue(Files.exists(ARCHIVE_ZIP_PATH));
        Assertions.assertTrue(Files.size(ARCHIVE_ZIP_PATH) > 0);
    }

    @Test
    public void unzip_validZipFile_success() throws Exception {
        FileUtil.unzip(TEST_ZIP_PATH, UNZIPPED_DIRECTORY_PATH);
        Assertions.assertTrue(Files.exists(UNZIPPED_DIRECTORY_PATH));
        Assertions.assertTrue(TestUtil.compareDirectories(EXPECTED_UNZIPPED_DIRECTORY_PATH, UNZIPPED_DIRECTORY_PATH));
    }

    @Test
    public void unzip_invalidZipFile_fail() throws Exception {
        Path invalidZipFile = Paths.get(FILE_UTIL_TEST_DIRECTORY.toString(), "test.csv");
        FileUtil.unzip(invalidZipFile, FILE_UTIL_TEST_DIRECTORY);
        Assertions.assertFalse(Files.exists(Paths.get(FILE_UTIL_TEST_DIRECTORY.toString(), "test")));
    }

    @Test
    public void deleteFileFromZipFile_success() throws Exception {
        long originalZipFileSize = Files.size(TEST_ZIP_PATH);

        File testFile = TEST_FILE_PATH.toFile();

        boolean fileCreated = testFile.createNewFile();
        if (!fileCreated) {
            throw new IOException("Test file cannot be created.");
        }

        FileUtil.addOrReplaceFileInZipFile(FILE_UTIL_TEST_DIRECTORY, FILE_UTIL_TEST_DIRECTORY, TEST_FILE_NAME,
                TEST_ZIP_FILE_NAME);

        FileUtil.deleteFileFromZipFile(TEST_ZIP_PATH, TEST_FILE_NAME);

        Assertions.assertEquals(originalZipFileSize, Files.size(TEST_ZIP_PATH));
    }

    @Test
    public void deleteFileFromZipFile_invalidFile_fail() throws Exception {
        long originalZipFileSize = Files.size(TEST_ZIP_PATH);
        FileUtil.deleteFileFromZipFile(TEST_ZIP_PATH, "invalidFileName.txt");
        Assertions.assertEquals(originalZipFileSize, Files.size(TEST_ZIP_PATH));
    }

    @Test
    public void addOrReplaceFileInZipFile_invalidFile_fail() throws Exception {
        long originalZipFileSize = Files.size(TEST_ZIP_PATH);
        FileUtil.addOrReplaceFileInZipFile(FILE_UTIL_TEST_DIRECTORY, FILE_UTIL_TEST_DIRECTORY,
                        "invalidFileName.txt", TEST_ZIP_FILE_NAME);
        Assertions.assertEquals(originalZipFileSize, Files.size(TEST_ZIP_PATH));
    }

    @Test
    public void addOrReplaceFileInZipFile_replaceFile_success() throws Exception {
        File testFile = TEST_FILE_PATH.toFile();

        boolean fileCreated = testFile.createNewFile();
        if (!fileCreated) {
            throw new IOException("Test file cannot be created.");
        }

        FileUtil.addOrReplaceFileInZipFile(FILE_UTIL_TEST_DIRECTORY, FILE_UTIL_TEST_DIRECTORY, TEST_FILE_NAME,
                TEST_ZIP_FILE_NAME);

        long originalZipFileSize = Files.size(TEST_ZIP_PATH);


        FileWriter writer = new FileWriter(testFile);
        writer.write("Hello, this is text written to a file!");
        writer.close();

        FileUtil.addOrReplaceFileInZipFile(FILE_UTIL_TEST_DIRECTORY, FILE_UTIL_TEST_DIRECTORY, TEST_FILE_NAME,
                TEST_ZIP_FILE_NAME);


        Assertions.assertTrue(originalZipFileSize < Files.size(TEST_ZIP_PATH));
    }

    @Test
    public void addOrReplaceFileInZipFile_addFile_success() throws Exception {
        File testFile = TEST_FILE_PATH.toFile();

        long originalZipFileSize = Files.size(TEST_ZIP_PATH);
        boolean fileCreated = testFile.createNewFile();
        if (!fileCreated) {
            throw new IOException("Test file cannot be created.");
        }

        FileUtil.addOrReplaceFileInZipFile(FILE_UTIL_TEST_DIRECTORY, FILE_UTIL_TEST_DIRECTORY, TEST_FILE_NAME,
                TEST_ZIP_FILE_NAME);


        Assertions.assertTrue(originalZipFileSize < Files.size(TEST_ZIP_PATH));
    }


    @AfterEach
    public void after() throws Exception {
        FileUtil.deleteFileFromZipFile(TEST_ZIP_PATH, TEST_FILE_NAME);
        Files.deleteIfExists(TEST_FILE_PATH);
        Files.deleteIfExists(ARCHIVE_ZIP_PATH);
        if (Files.exists(UNZIPPED_DIRECTORY_PATH)) {
            FileUtil.deleteDirectory(UNZIPPED_DIRECTORY_PATH.toString());
        }
    }
}
