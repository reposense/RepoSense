package reposense.system;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.freeutils.httpserver.HTTPServer;

/**
 * Handles starting of the server to display the report.
 */
public class ReportServer {

    private static final String LOCAL_HOST_URL = "http://localhost:%s/";

    private static final Logger logger = LogsManager.getLogger(ReportServer.class);

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
            host.addContext("/", new HTTPServer.FileContextHandler(requestPath.toFile()));
            server.start();
            launchBrowser(String.format(LOCAL_HOST_URL, port));
            logger.info("Press Ctrl + C or equivalent to stop the server");
        } catch (IOException ioe) {
            logger.log(Level.SEVERE, ioe.getMessage(), ioe);
        }
    }

    /**
     * Launches the default browser with {@code url}.
     */
    private static void launchBrowser(String url) throws IOException {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
                logger.info("Loading " + url + " on the default browser...");
            } else {
                logger.severe("Browser could not be launched. Please refer to the user guide to"
                        + " manually view the report");
            }
        } catch (URISyntaxException ue) {
            logger.log(Level.SEVERE, ue.getMessage(), ue);
        }
    }
}
