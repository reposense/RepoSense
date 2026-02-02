package reposense.wizard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import net.freeutils.httpserver.HTTPServer;
import net.freeutils.httpserver.HTTPServer.ContextHandler;
import net.freeutils.httpserver.HTTPServer.Request;
import net.freeutils.httpserver.HTTPServer.Response;
import reposense.model.RepoLocation;
import reposense.system.LogsManager;

/**
 * Handles starting of the server to display the config wizard.
 */
public class ConfigWizardServer {
    private static final Logger logger = LogsManager.getLogger(ConfigWizardServer.class);
    private static final Path BUILD_PATH = Paths.get(System.getProperty("user.dir"), "frontend", "build");

    /**
     * Starts the config wizard server at {@code port}.
     */
    public static void startWizard(int port) {
        logger.info("Starting Config Wizard...");
        HTTPServer server = new HTTPServer(port);
        HTTPServer.VirtualHost host = server.getVirtualHost(null);

        try {
            // Serve all files from build directory
            host.addContext("/", new HTTPServer.FileContextHandler(BUILD_PATH.toFile()));

            // API endpoints
            host.addContext("/api", new ApiHandler(), "GET", "POST");

            server.start();
            // Launch directly to the wizard index
            launchBrowser(String.format("http://localhost:%s/wizard/", port));
            logger.info("Press Ctrl + C or equivalent to stop the server");
        } catch (IOException ioe) {
            logger.log(Level.SEVERE, ioe.getMessage(), ioe);
        }
    }

    /**
     * Launches the default browser with {@code url}.
     */
    private static void launchBrowser(String url) {
        try {
            if (java.awt.Desktop.isDesktopSupported()
                    && java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
                logger.info("Loading " + url + " on the default browser...");
            }
        } catch (Exception e) {
            logger.severe("Browser could not be launched: " + e.getMessage());
        }
    }

    /**
     * Handler for REST API requests.
     */
    private static class ApiHandler implements ContextHandler {
        @Override
        public int serve(Request req, Response resp) throws IOException {
            String path = req.getPath();
            resp.getHeaders().add("Content-Type", "application/json");

            if (path.equals("/api/config") && req.getMethod().equals("GET")) {
                resp.send(200, "{\"status\": \"ok\", \"config\": {}}");
                return 200;
            }

            if (path.equals("/api/validate") && req.getMethod().equals("POST")) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(req.getBody()))) {
                    String body = reader.lines().collect(Collectors.joining("\n"));
                    JsonObject jsonBody = JsonParser.parseString(body).getAsJsonObject();
                    String location = jsonBody.get("location").getAsString();

                    try {
                        new RepoLocation(location);
                        resp.send(200, "{\"valid\": true}");
                    } catch (Exception e) {
                        resp.send(200, "{\"valid\": false, \"error\": \"" + e.getMessage() + "\"}");
                    }
                    return 200;
                } catch (Exception e) {
                    resp.send(400, "{\"error\": \"Invalid request body\"}");
                    return 400;
                }
            }

            if (path.equals("/api/generate") && req.getMethod().equals("POST")) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(req.getBody()))) {
                    String body = reader.lines().collect(Collectors.joining("\n"));
                    JsonObject jsonBody = JsonParser.parseString(body).getAsJsonObject();

                    Gson gson = new Gson();
                    List<Map<String, Object>> repos = gson.fromJson(jsonBody.get("repos"),
                            new TypeToken<List<Map<String, Object>>>() {
                            }.getType());
                    List<Map<String, Object>> authors = gson.fromJson(jsonBody.get("authors"),
                            new TypeToken<List<Map<String, Object>>>() {
                            }.getType());
                    List<Map<String, Object>> groups = gson.fromJson(jsonBody.get("groups"),
                            new TypeToken<List<Map<String, Object>>>() {
                            }.getType());
                    Map<String, Object> report = gson.fromJson(jsonBody.get("report"),
                            new TypeToken<Map<String, Object>>() {
                            }.getType());

                    Path outputDir = Paths.get(System.getProperty("user.dir"), "generated-configs");
                    Files.createDirectories(outputDir);

                    ConfigFileWriter.writeRepoConfig(repos, outputDir.resolve("repo-config.csv"));
                    ConfigFileWriter.writeAuthorConfig(authors, outputDir.resolve("author-config.csv"));
                    ConfigFileWriter.writeGroupConfig(groups, outputDir.resolve("group-config.csv"));
                    ConfigFileWriter.writeReportConfig(report, outputDir.resolve("report-config.yaml"));

                    resp.send(200, "{\"success\": true, \"path\": \"" + outputDir.toString() + "\"}");
                    return 200;
                } catch (Exception e) {
                    resp.send(500, "{\"error\": \"" + e.getMessage() + "\"}");
                    return 500;
                }
            }

            resp.send(404, "{\"error\": \"Not Found\"}");
            return 404;
        }
    }
}
