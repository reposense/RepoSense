package reposense.template;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import reposense.system.LogsManager;
import reposense.util.FileUtil;

/**
 * Ensures the shared temporary repo base path used by tests is cleaned up after
 * all participating test classes finish.
 */
public abstract class RepoTempCleanupTestTemplate {
    private static final Logger logger = LogsManager.getLogger(RepoTempCleanupTestTemplate.class);
    private static final String REPOS_TEMP_PREFIX = FileUtil.REPOS_TEMP_PREFIX;
    private static final AtomicInteger ACTIVE_TEST_CLASSES = new AtomicInteger(0);

    @BeforeAll
    public static void registerTempRepoUsage() {
        ACTIVE_TEST_CLASSES.incrementAndGet();
    }

    @AfterAll
    public static void cleanupTempRepoBasePath() {
        if (ACTIVE_TEST_CLASSES.decrementAndGet() == 0) {
            FileUtil.cleanupRepoBasePath();
            cleanupStaleTempRepoDirectories();
        }
    }

    private static void cleanupStaleTempRepoDirectories() {
        Path workingDirectory = Paths.get(System.getProperty("user.dir"));
        try (Stream<Path> children = Files.list(workingDirectory)) {
            children
                    .filter(Files::isDirectory)
                    .filter(path -> path.getFileName().toString().startsWith(REPOS_TEMP_PREFIX))
                    .forEach(path -> {
                        try {
                            FileUtil.deleteDirectory(path.toString());
                        } catch (IOException e) {
                            logger.log(Level.WARNING, "Failed to delete stale temp directory: " + path, e);
                        }
                    });
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to scan for stale temp directories.", e);
        }
    }
}
