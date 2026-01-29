package reposense.wizard;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import reposense.system.LogsManager;
import reposense.system.ReportServer;

/**
 * Handles starting of the server to display the config wizard.
 */
public class ConfigWizardServer {
    private static final Logger logger = LogsManager.getLogger(ConfigWizardServer.class);
    private static final Path WIZARD_ASSETS_PATH = Paths.get(System.getProperty("user.dir"), "frontend", "build",
            "wizard");

    /**
     * Starts the config wizard server at {@code port}.
     */
    public static void startWizard(int port) {
        logger.info("Starting Config Wizard...");
        // In the final version, this will point to the built dist folder.
        // For now, we point to the source directory to verify the server starts.
        ReportServer.startServer(port, WIZARD_ASSETS_PATH);
    }
}
