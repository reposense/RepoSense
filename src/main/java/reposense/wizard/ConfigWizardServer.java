package reposense.wizard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
import reposense.system.LogsManager;
import reposense.util.FileUtil;

/**
 * Handles starting of the server to display the config wizard.
 */
public class ConfigWizardServer {
    private static final Logger logger = LogsManager.getLogger(ConfigWizardServer.class);

    /**
     * Starts the config wizard server at {@code port} and opens the browser.
     */
    public static void startWizard(int port) {
        startWizard(port, true);
    }

    /**
     * Starts the config wizard server at {@code port}.
     * If {@code openBrowser} is true, the default browser is opened at the wizard URL.
     */
    public static void startWizard(int port, boolean openBrowser) {
        logger.info("Starting Config Wizard...");
        HTTPServer server = new HTTPServer(port);
        HTTPServer.VirtualHost host = server.getVirtualHost(null);

        // Always register the API handler — independent of static file serving.
        host.addContext("/api", new ApiHandler(), "GET", "POST");

        // Try to serve static wizard assets; log error but don't abort server start.
        try {
            Path buildPath = extractWizardAssets();
            host.addContext("/", new HTTPServer.FileContextHandler(buildPath.toFile()));
        } catch (IOException ioe) {
            logger.log(Level.SEVERE, "Failed to extract wizard assets; static files will not be served.", ioe);
        }

        try {
            server.start();
            if (openBrowser) {
                launchBrowser(String.format("http://localhost:%s/config-wizard", port));
            }
            logger.info("Press Ctrl + C or equivalent to stop the server");
        } catch (IOException ioe) {
            logger.log(Level.SEVERE, ioe.getMessage(), ioe);
        }
    }

    /**
     * Extracts wizard frontend assets from the bundled {@code templateZip.zip} on the classpath
     * to a temporary directory and returns that path. Falls back to {@code frontend/build} in the
     * working directory when running from source (dev mode).
     *
     * @throws IOException if a temporary directory cannot be created or the zip cannot be extracted
     */
    private static Path extractWizardAssets() throws IOException {
        InputStream is = ConfigWizardServer.class.getResourceAsStream("/templateZip.zip");
        if (is != null) {
            Path tempDir = Files.createTempDirectory("reposense-wizard-");
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    FileUtil.deleteDirectory(tempDir.toString());
                } catch (Exception e) {
                    logger.warning("Failed to delete wizard temp directory: " + e.getMessage());
                }
            }));
            FileUtil.unzip(is, tempDir);
            return tempDir;
        }
        // Dev fallback: serve directly from the frontend build output
        return Paths.get(System.getProperty("user.dir"), "frontend", "build");
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
     * Handler for REST API requests, dispatching to per-route methods via a route map.
     */
    private static class ApiHandler implements ContextHandler {

        @FunctionalInterface
        private interface RouteHandler {
            int handle(Request req, Response resp) throws IOException;
        }

        private final ConfigWizardService service = new ConfigWizardService();
        private final Map<String, RouteHandler> routes;

        ApiHandler() {
            routes = new HashMap<>();
            routes.put("GET /api/config", this::handleGetConfig);
            routes.put("POST /api/validate", this::handleValidate);
            routes.put("POST /api/validate-glob", this::handleValidateGlob);
            routes.put("POST /api/validate-config", this::handleValidateConfig);
            routes.put("POST /api/generate", this::handleGenerate);
            routes.put("POST /api/preview", this::handlePreview);
            routes.put("POST /api/quit", this::handleQuit);
        }

        @Override
        public int serve(Request req, Response resp) throws IOException {
            String key = req.getMethod() + " " + req.getPath();
            resp.getHeaders().add("Content-Type", "application/json");
            RouteHandler handler = routes.get(key);
            if (handler == null) {
                resp.send(404, "{\"error\": \"Not Found\"}");
                return 404;
            }
            return handler.handle(req, resp);
        }

        private String readBody(Request req) throws IOException {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(req.getBody()))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        }

        /** Escapes a string for safe embedding in a JSON value. */
        private static String escapeJson(String s) {
            return s.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", " ")
                    .replace("\r", "");
        }

        /**
         * Handles {@code GET /api/config} — returns server status.
         *
         * @throws IOException if sending the response fails
         */
        private int handleGetConfig(Request req, Response resp) throws IOException {
            resp.send(200, "{\"status\": \"ok\", \"config\": {}}");
            return 200;
        }

        /**
         * Handles {@code POST /api/validate} — validates a repository location.
         *
         * @throws IOException if reading the request body or sending the response fails
         */
        private int handleValidate(Request req, Response resp) throws IOException {
            try {
                JsonObject json = JsonParser.parseString(readBody(req)).getAsJsonObject();
                String location = json.get("location").getAsString();
                Optional<String> error = service.validateLocation(location);
                if (!error.isPresent()) {
                    resp.send(200, "{\"valid\": true}");
                } else {
                    resp.send(200, "{\"valid\": false, \"error\": \"" + escapeJson(error.get()) + "\"}");
                }
                return 200;
            } catch (Exception e) {
                resp.send(400, "{\"error\": \"Invalid request body\"}");
                return 400;
            }
        }

        /**
         * Handles {@code POST /api/validate-glob} — validates a glob pattern.
         *
         * @throws IOException if reading the request body or sending the response fails
         */
        private int handleValidateGlob(Request req, Response resp) throws IOException {
            try {
                JsonObject json = JsonParser.parseString(readBody(req)).getAsJsonObject();
                String pattern = json.get("pattern").getAsString();
                Optional<String> error = service.validateGlob(pattern);
                if (!error.isPresent()) {
                    resp.send(200, "{\"valid\": true}");
                } else {
                    resp.send(200, "{\"valid\": false, \"error\": \"" + escapeJson(error.get()) + "\"}");
                }
                return 200;
            } catch (Exception e) {
                resp.send(400, "{\"error\": \"Invalid request body\"}");
                return 400;
            }
        }

        /**
         * Handles {@code POST /api/validate-config} — validates the full wizard config via the RepoSense parser.
         *
         * @throws IOException if reading the request body or sending the response fails
         */
        private int handleValidateConfig(Request req, Response resp) throws IOException {
            Map<String, Object> config;
            try {
                config = new Gson().fromJson(readBody(req),
                        new TypeToken<Map<String, Object>>() {}.getType());
            } catch (Exception e) {
                resp.send(400, "{\"error\": \"Invalid request body\"}");
                return 400;
            }
            try {
                Optional<String> error = service.validateConfig(config);
                if (!error.isPresent()) {
                    resp.send(200, "{\"valid\": true}");
                } else {
                    resp.send(200, "{\"valid\": false, \"error\": \"" + escapeJson(error.get()) + "\"}");
                }
                return 200;
            } catch (Exception e) {
                resp.send(500, "{\"error\": \"" + escapeJson(String.valueOf(e.getMessage())) + "\"}");
                return 500;
            }
        }

        /**
         * Handles {@code POST /api/generate} — writes {@code report-config.yaml} and returns its path.
         *
         * @throws IOException if reading the request body or sending the response fails
         */
        private int handleGenerate(Request req, Response resp) throws IOException {
            try {
                Map<String, Object> config = new Gson().fromJson(readBody(req),
                        new TypeToken<Map<String, Object>>() {}.getType());
                Path outputPath = service.generateConfig(config);
                resp.send(200, "{\"success\": true, \"path\": \"" + escapeJson(outputPath.toString()) + "\"}");
                return 200;
            } catch (Exception e) {
                resp.send(500, "{\"error\": \"" + escapeJson(String.valueOf(e.getMessage())) + "\"}");
                return 500;
            }
        }

        /**
         * Handles {@code POST /api/preview} — returns a YAML preview string for the current config.
         *
         * @throws IOException if reading the request body or sending the response fails
         */
        private int handlePreview(Request req, Response resp) throws IOException {
            try {
                Map<String, Object> config = new Gson().fromJson(readBody(req),
                        new TypeToken<Map<String, Object>>() {}.getType());
                String yaml = service.previewConfig(config);
                String escaped = yaml.replace("\\", "\\\\").replace("\"", "\\\"")
                        .replace("\n", "\\n").replace("\r", "");
                resp.send(200, "{\"yaml\": \"" + escaped + "\"}");
                return 200;
            } catch (Exception e) {
                resp.send(500, "{\"error\": \"" + escapeJson(String.valueOf(e.getMessage())) + "\"}");
                return 500;
            }
        }

        /**
         * Handles {@code POST /api/quit} — shuts down the server after a short delay.
         *
         * @throws IOException if sending the response fails
         */
        private int handleQuit(Request req, Response resp) throws IOException {
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
    }
}
