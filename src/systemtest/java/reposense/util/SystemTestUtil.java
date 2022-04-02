package reposense.util;

import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Contains utility methods for system tests.
 */
public class SystemTestUtil {

    /**
     * Verifies that all JSON files in the {@code actualDirectory} matches those at the {@code expectedDirectory}.
     */
    public static void verifyAllJson(Path expectedDirectory, Path actualDirectory) {
        try (Stream<Path> pathStream = Files.list(expectedDirectory)) {
            for (Path file : pathStream.collect(Collectors.toList())) {
                Path expectedFilePath = expectedDirectory.resolve(file);
                Path actualFilePath = actualDirectory.resolve(file);
                if (Files.isDirectory(file)) {
                    verifyAllJson(expectedFilePath, actualFilePath);
                }
                if (file.toString().endsWith(".json")) {
                    assertJson(expectedFilePath, actualFilePath);
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
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
