package reposense.system;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import net.freeutils.httpserver.HTTPServer;
import net.freeutils.httpserver.HTTPServer.Request;
import net.freeutils.httpserver.HTTPServer.Response;

/**
 * Serves static files from disk like FileContextHandler,
 * but handles optional files gracefully by suppressing 404 errors.
 * For listed optional paths, responds with 204 No Content if file is missing.
 */
public class GracefulFileHandler implements HTTPServer.ContextHandler {

    private final File rootDirectory;
    private final Set<String> optionalPaths;

    public GracefulFileHandler(File rootDirectory, Set<String> optionalPaths) {
        this.rootDirectory = rootDirectory;
        this.optionalPaths = optionalPaths;
    }

    @Override
    public int serve(Request request, Response response) throws IOException {
        String reqPath = request.getPath();

        // If file is optional and doesn't exist, return 204 No Content
        File targetFile = new File(rootDirectory, reqPath);

        if (optionalPaths.contains(reqPath) && !targetFile.exists()) {
            System.out.println("Optional file missing: " + reqPath + " — returning 204");
            response.getHeaders().add("Content-Type", "text/plain");
            response.send(204, "");
            return 0;
        }

        // Fall back to normal file serving
        HTTPServer.ContextHandler fileHandler = new HTTPServer.FileContextHandler(rootDirectory);
        return fileHandler.serve(request, response);
    }
}
