package reposense.wizard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import net.freeutils.httpserver.HTTPServer;
import net.freeutils.httpserver.HTTPServer.ContextHandler;
import net.freeutils.httpserver.HTTPServer.Request;
import net.freeutils.httpserver.HTTPServer.Response;
import reposense.model.RepoLocation;
import reposense.parser.ReportConfigYamlParser;
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
            host.addContext("/", new HTTPServer.FileContextHandler(BUILD_PATH.toFile()));
            host.addContext("/api", new ApiHandler(), "GET", "POST");

            server.start();
            launchBrowser(String.format("http://localhost:%s/config-wizard", port));
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
     * Escapes a string for safe embedding in a JSON value.
     */
    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", " ")
                .replace("\r", "");
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
                        resp.send(200, "{\"valid\": false, \"error\": \""
                                + escapeJson(e.getMessage()) + "\"}");
                    }
                    return 200;
                } catch (Exception e) {
                    resp.send(400, "{\"error\": \"Invalid request body\"}");
                    return 400;
                }
            }

            if (path.equals("/api/validate-glob") && req.getMethod().equals("POST")) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(req.getBody()))) {
                    String body = reader.lines().collect(Collectors.joining("\n"));
                    JsonObject jsonBody = JsonParser.parseString(body).getAsJsonObject();
                    String pattern = jsonBody.get("pattern").getAsString();
                    try {
                        FileSystems.getDefault().getPathMatcher("glob:" + pattern);
                        resp.send(200, "{\"valid\": true}");
                    } catch (PatternSyntaxException e) {
                        resp.send(200, "{\"valid\": false, \"error\": \""
                                + escapeJson(e.getMessage()) + "\"}");
                    }
                    return 200;
                } catch (Exception e) {
                    resp.send(400, "{\"error\": \"Invalid request body\"}");
                    return 400;
                }
            }

            if (path.equals("/api/validate-config") && req.getMethod().equals("POST")) {
                Path tempFile = null;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(req.getBody()))) {
                    String body = reader.lines().collect(Collectors.joining("\n"));
                    Gson gson = new Gson();
                    Map<String, Object> config = gson.fromJson(body,
                            new TypeToken<Map<String, Object>>() {}.getType());

                    tempFile = Files.createTempFile("reposense-wizard-", ".yaml");
                    ConfigFileWriter.writeReportConfig(config, tempFile);

                    try {
                        new ReportConfigYamlParser().parse(tempFile);
                        resp.send(200, "{\"valid\": true}");
                    } catch (Exception e) {
                        String msg = e.getMessage() != null ? escapeJson(e.getMessage()) : "Invalid configuration";
                        resp.send(200, "{\"valid\": false, \"error\": \"" + msg + "\"}");
                    }
                    return 200;
                } catch (Exception e) {
                    resp.send(500, "{\"error\": \"" + escapeJson(String.valueOf(e.getMessage())) + "\"}");
                    return 500;
                } finally {
                    if (tempFile != null) {
                        try {
                            Files.deleteIfExists(tempFile);
                        } catch (IOException ignored) {
                            // ignored
                        }
                    }
                }
            }

            if (path.equals("/api/generate") && req.getMethod().equals("POST")) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(req.getBody()))) {
                    String body = reader.lines().collect(Collectors.joining("\n"));
                    Gson gson = new Gson();
                    Map<String, Object> config = gson.fromJson(body,
                            new TypeToken<Map<String, Object>>() {}.getType());

                    Path outputDir = Paths.get(System.getProperty("user.dir"), "generated-configs");
                    Path outputPath = outputDir.resolve("report-config.yaml");
                    ConfigFileWriter.writeReportConfig(config, outputPath);

                    resp.send(200, "{\"success\": true, \"path\": \"" + outputPath.toString() + "\"}");
                    return 200;
                } catch (Exception e) {
                    resp.send(500, "{\"error\": \"" + escapeJson(String.valueOf(e.getMessage())) + "\"}");
                    return 500;
                }
            }

            if (path.equals("/api/preview") && req.getMethod().equals("POST")) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(req.getBody()))) {
                    String body = reader.lines().collect(Collectors.joining("\n"));
                    Gson gson = new Gson();
                    Map<String, Object> config = gson.fromJson(body,
                            new TypeToken<Map<String, Object>>() {}.getType());

                    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                    String yaml = mapper.writeValueAsString(config);

                    String escaped = yaml.replace("\\", "\\\\").replace("\"", "\\\"")
                            .replace("\n", "\\n").replace("\r", "");
                    resp.send(200, "{\"yaml\": \"" + escaped + "\"}");
                    return 200;
                } catch (Exception e) {
                    resp.send(500, "{\"error\": \"" + escapeJson(String.valueOf(e.getMessage())) + "\"}");
                    return 500;
                }
            }

            if (path.equals("/api/quit") && req.getMethod().equals("POST")) {
                resp.send(200, "{\"ok\": true}");
                new Thread(() -> {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    System.exit(0);
                }).start();
                return 200;
            }

            resp.send(404, "{\"error\": \"Not Found\"}");
            return 404;
        }
    }
}
