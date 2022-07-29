package reposense.util;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Contains utility methods for system tests.
 */
public class SystemTestUtil {

    private static final String[] JSON_FIELDS_TO_IGNORE = new String[]
            {"repoSenseVersion", "reportGeneratedTime", "reportGenerationTime"};

    /**
     * Verifies that all JSON files in the {@code actualDirectory} matches those at the {@code expectedDirectory}.
     */
    public static void verifyReportJsonFiles(Path expectedDirectory, Path actualDirectory) {
        try (Stream<Path> pathStream = Files.list(expectedDirectory)) {
            for (Path file : pathStream.collect(Collectors.toList())) {
                Path expectedFilePath = expectedDirectory.resolve(file.getFileName());
                Path actualFilePath = actualDirectory.resolve(file.getFileName());
                if (Files.isDirectory(file)) {
                    verifyReportJsonFiles(expectedFilePath, actualFilePath);
                } else if (file.toString().endsWith(".json")) {
                    if (file.getFileName().toString().equals("summary.json")) {
                        assertSummaryJson(expectedFilePath, actualFilePath);
                    } else {
                        assertJson(expectedFilePath, actualFilePath);
                    }
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void assertSummaryJson(Path expectedSummaryJsonPath, Path actualSummaryJsonPath)
            throws IOException {
        try (FileReader fileReaderExpected = new FileReader(expectedSummaryJsonPath.toFile());
                FileReader fileReaderActual = new FileReader(actualSummaryJsonPath.toFile())) {
            JsonObject jsonExpected = JsonParser.parseReader(fileReaderExpected).getAsJsonObject();
            JsonObject jsonActual = JsonParser.parseReader(fileReaderActual).getAsJsonObject();
            for (String ignoredKey : JSON_FIELDS_TO_IGNORE) {
                jsonExpected.remove(ignoredKey);
                jsonActual.remove(ignoredKey);
            }
            Assertions.assertEquals(jsonExpected, jsonActual);
        }
    }

    /**
     * Asserts that the contents in the given JSON file at {@code actualJsonPath} is the same as the JSON file
     * at {@code expectedJsonPath}.
     */
    public static void assertJson(Path expectedJsonPath, Path actualJsonPath) {
        Assertions.assertTrue(Files.exists(actualJsonPath));
        try {
            Assertions.assertTrue(TestUtil.compareFileContents(expectedJsonPath, actualJsonPath));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }
}
