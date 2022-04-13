package reposense.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Assertions;

/**
 * Contains utility methods for system tests.
 */
public class SystemTestUtil {

    private static final String[] JSON_FIELDS_TO_IGNORE = new String[]
            {"repoSenseVersion", "reportGeneratedTime", "reportGenerationTime", "zoneId"};

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
                    System.out.println(file.getFileName());
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
        JsonObject jsonExpected = JsonParser.parseReader(
                new FileReader(expectedSummaryJsonPath.toFile())).getAsJsonObject();
        JsonObject jsonActual = JsonParser.parseReader(
                new FileReader(actualSummaryJsonPath.toFile())).getAsJsonObject();
        for (String ignoredKey : JSON_FIELDS_TO_IGNORE) {
            jsonExpected.remove(ignoredKey);
            jsonActual.remove(ignoredKey);
        }
        Assertions.assertEquals(jsonExpected, jsonActual);
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
