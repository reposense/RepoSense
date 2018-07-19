package reposense.system;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.freeutils.httpserver.HTTPServer;
import reposense.RepoSense;

/**
 * Represents the RepoSense dashboard server
 */
public class DashboardServer {

    private static final String LOCAL_HOST_URL = "http://localhost:%s/";

    private static final Logger logger = LogsManager.getLogger(RepoSense.class);

    /**
     * Starts a server at {@code port} and loads the {@code requestPath} from the local disk.
     */
    public static void startServer(int port, Path requestPath) {
        logger.info("Starting a server at port " + port + "...");
        HTTPServer server = new HTTPServer(port);

        // default virtual host
        HTTPServer.VirtualHost host = server.getVirtualHost(null);

        try {
            // a handler to process the request and give the corresponding response
            host.addContext(File.separator, new HTTPServer.FileContextHandler(requestPath.toFile()));
            server.start();
            launchBrowser(String.format(LOCAL_HOST_URL, port));
        } catch (IOException ioe) {
            logger.log(Level.SEVERE, ioe.getMessage(), ioe);
        }
    }

    /**
     * Launches the default browser with {@code Url}.
     */
    private static void launchBrowser(String Url) throws IOException {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(Url));
                logger.info("Loading " + Url + " on the default browser...");
                logger.info("Press Ctrl + C or equivalent to exit");
            }
        } catch (URISyntaxException ue) {
            logger.log(Level.SEVERE, ue.getMessage(), ue);
        }
    }
}
