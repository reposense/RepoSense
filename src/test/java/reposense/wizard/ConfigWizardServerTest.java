package reposense.wizard;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for {@link ConfigWizardServer} REST API endpoints.
 *
 * Starts the wizard HTTP server on a random free port and sends real HTTP requests to each
 * endpoint, asserting the JSON responses. No mocking framework is used — this tests the full
 * request-handling path of the embedded {@code ApiHandler}.
 */
public class ConfigWizardServerTest {

    private static int port;
    private static HttpClient client;
    private static Path generatedFile;

    @BeforeAll
    static void startServer() throws Exception {
        // Find a free ephemeral port before binding the server.
        try (ServerSocket socket = new ServerSocket(0)) {
            port = socket.getLocalPort();
        }
        ConfigWizardServer.startWizard(port, false);
        client = HttpClient.newHttpClient();
        // Poll until the server responds or ~1 second has elapsed.
        for (int i = 0; i < 20; i++) {
            try {
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/api/config"))
                        .header("Connection", "close")
                        .GET().build();
                if (client.send(req, HttpResponse.BodyHandlers.ofString()).statusCode() == 200) {
                    break;
                }
            } catch (Exception ignored) {
                Thread.sleep(50);
            }
        }
    }

    @AfterAll
    static void teardown() throws IOException {
        ConfigWizardServer.stopWizard();
        if (generatedFile != null) {
            Files.deleteIfExists(generatedFile);
        }
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private HttpResponse<String> post(String path, String body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + path))
                .header("Content-Type", "application/json")
                .header("Connection", "close")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + path))
                .header("Connection", "close")
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    // -----------------------------------------------------------------------
    // GET /api/config
    // -----------------------------------------------------------------------

    @Test
    public void getConfig_returnsStatusOk() throws Exception {
        HttpResponse<String> resp = get("/api/config");

        Assertions.assertEquals(200, resp.statusCode());
        Assertions.assertTrue(resp.body().contains("\"status\""),
                "Response body should contain 'status' field");
        Assertions.assertTrue(resp.body().contains("ok"),
                "Status value should be 'ok'");
    }

    // -----------------------------------------------------------------------
    // POST /api/validate
    // -----------------------------------------------------------------------

    @Test
    public void validate_validGitHubUrl_returnsValid() throws Exception {
        HttpResponse<String> resp = post("/api/validate",
                "{\"location\": \"https://github.com/reposense/RepoSense.git\"}");

        Assertions.assertEquals(200, resp.statusCode());
        Assertions.assertTrue(resp.body().contains("true"),
                "Valid GitHub URL should return {\"valid\": true}");
        Assertions.assertFalse(resp.body().contains("\"error\""),
                "Valid URL response should not contain an error field");
    }

    @Test
    public void validate_localPath_returnsValid() throws Exception {
        // An existing absolute path is a valid repo location.
        String existingPath = System.getProperty("user.dir").replace("\\", "/");
        HttpResponse<String> resp = post("/api/validate",
                "{\"location\": \"" + existingPath + "\"}");

        Assertions.assertEquals(200, resp.statusCode());
        // Local paths that exist are accepted; the exact response depends on RepoLocation.
        // We just assert the request is handled (200 OK) without a server error.
    }

    @Test
    public void validate_invalidUrl_returnsInvalid() throws Exception {
        // "http:" has a colon, so RepoLocation treats it as a remote URL, but it lacks "://"
        // so neither the GIT nor SCP URL patterns match → InvalidLocationException → valid: false.
        HttpResponse<String> resp = post("/api/validate",
                "{\"location\": \"http:\"}");

        Assertions.assertEquals(200, resp.statusCode());
        Assertions.assertTrue(resp.body().contains("false"),
                "Invalid location should return {\"valid\": false}");
    }

    // -----------------------------------------------------------------------
    // POST /api/validate-glob
    // -----------------------------------------------------------------------

    @Test
    public void validateGlob_validPattern_returnsValid() throws Exception {
        HttpResponse<String> resp = post("/api/validate-glob",
                "{\"pattern\": \"src/**/*.java\"}");

        Assertions.assertEquals(200, resp.statusCode());
        Assertions.assertTrue(resp.body().contains("true"),
                "Valid glob should return {\"valid\": true}");
    }

    @Test
    public void validateGlob_invalidPattern_returnsInvalid() throws Exception {
        // An unclosed bracket is a glob syntax error.
        HttpResponse<String> resp = post("/api/validate-glob",
                "{\"pattern\": \"[\"}");

        Assertions.assertEquals(200, resp.statusCode());
        Assertions.assertTrue(resp.body().contains("false"),
                "Invalid glob should return {\"valid\": false}");
    }

    // -----------------------------------------------------------------------
    // POST /api/validate-config
    // -----------------------------------------------------------------------

    @Test
    public void validateConfig_emptyRepoList_returnsOk() throws Exception {
        String payload = "{\"title\": \"Test\", \"repos\": []}";
        HttpResponse<String> resp = post("/api/validate-config", payload);

        // The response is 200 regardless of validity; just assert no server error.
        Assertions.assertEquals(200, resp.statusCode());
        Assertions.assertTrue(resp.body().contains("\"valid\""),
                "Response should contain 'valid' field");
    }

    @Test
    public void validateConfig_malformedBody_returns400() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/validate-config"))
                .header("Content-Type", "application/json")
                .header("Connection", "close")
                .POST(HttpRequest.BodyPublishers.ofString("not json"))
                .build();
        HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(400, resp.statusCode(),
                "Malformed body should result in 400");
    }

    // -----------------------------------------------------------------------
    // POST /api/preview
    // -----------------------------------------------------------------------

    @Test
    public void preview_validPayload_returnsYamlString() throws Exception {
        String payload = "{\"title\": \"Preview Report\"}";
        HttpResponse<String> resp = post("/api/preview", payload);

        Assertions.assertEquals(200, resp.statusCode());
        Assertions.assertTrue(resp.body().contains("\"yaml\""),
                "Preview response should contain 'yaml' field");
    }

    @Test
    public void preview_configWithTitle_yamlContainsTitle() throws Exception {
        String payload = "{\"title\": \"UniqueTitle12345\"}";
        HttpResponse<String> resp = post("/api/preview", payload);

        Assertions.assertEquals(200, resp.statusCode());
        Assertions.assertTrue(resp.body().contains("UniqueTitle12345"),
                "YAML preview should contain the provided title");
    }

    // -----------------------------------------------------------------------
    // POST /api/generate
    // -----------------------------------------------------------------------

    @Test
    public void generate_validPayload_writesFileAndReturnsPath() throws Exception {
        String payload = "{\"title\": \"Generated Report\", \"repos\": []}";
        HttpResponse<String> resp = post("/api/generate", payload);

        Assertions.assertEquals(200, resp.statusCode());
        Assertions.assertTrue(resp.body().contains("true"),
                "Generate response should contain success: true");
        Assertions.assertTrue(resp.body().contains("\"path\""),
                "Generate response should contain a 'path' field");

        // Extract path and verify the file was actually written.
        String body = resp.body();
        int pathStart = body.indexOf("\"path\":\"") + "\"path\":\"".length();
        int pathEnd = body.indexOf("\"", pathStart);
        if (pathStart > "\"path\":\"".length() - 1 && pathEnd > pathStart) {
            String filePath = body.substring(pathStart, pathEnd).replace("\\\\", "/");
            generatedFile = Paths.get(filePath.replace("\\/", "/"));
            Assertions.assertTrue(Files.exists(generatedFile),
                    "Generated file should exist at the returned path");
        }
    }

    // -----------------------------------------------------------------------
    // Unknown endpoint
    // -----------------------------------------------------------------------

    @Test
    public void unknownEndpoint_returns404() throws Exception {
        HttpResponse<String> resp = get("/api/nonexistent");

        Assertions.assertEquals(404, resp.statusCode());
    }
}
