package reposense.wizard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

/**
 * Handles starting of the server to display the config wizard.
 */
public class ConfigWizardServer {
    private static final Logger logger = LogsManager.getLogger(ConfigWizardServer.class);
    private static final Path BUILD_PATH = Paths.get(System.getProperty("user.dir"), "frontend", "build");

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

        try {
            host.addContext("/", new HTTPServer.FileContextHandler(BUILD_PATH.toFile()));
            host.addContext("/api", new ApiHandler(), "GET", "POST");

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

        private static String escapeJson(String s) {
            return s.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", " ")
                    .replace("\r", "");
        }

        private int handleGetConfig(Request req, Response resp) throws IOException {
            resp.send(200, "{\"status\": \"ok\", \"config\": {}}");
            return 200;
        }

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

        private int handleValidateConfig(Request req, Response resp) throws IOException {
            try {
                Map<String, Object> config = new Gson().fromJson(readBody(req),
                        new TypeToken<Map<String, Object>>() {}.getType());
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
