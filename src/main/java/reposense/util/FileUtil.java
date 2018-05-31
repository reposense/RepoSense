package reposense.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class FileUtil {

    public static void writeJsonFile(Object object, String path) {
        Gson gson = new GsonBuilder()
                .setDateFormat(Constants.GITHUB_API_DATE_FORMAT)
                .setPrettyPrinting()
                .create();
        String result = gson.toJson(object);

        try (PrintWriter out = new PrintWriter(path)) {
            out.print(result);
            out.print("\n");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getRepoDirectory(String org, String repoName) {
        return Constants.REPOS_ADDRESS + File.separator + org + File.separator + repoName + File.separator;
    }

    public static void deleteDirectory(String root) throws IOException {
        Path rootPath = Paths.get(root);
        if (Files.exists(rootPath)) {
            Files.walk(rootPath)
                    .sorted(Comparator.reverseOrder())
                    .forEach(FileUtil::deleteFile);
        }
    }

    private static void deleteFile(Path filePath) {
        try {
            // .git files created when cloning repo are `readonly`, so we need to set it to false in order to delete it
            Files.setAttribute(filePath, "dos:readonly", false);
            Files.delete(filePath);
        } catch (IOException ioe) {
            System.out.println("Error in deleting file " + filePath.toString());
        }
    }

    public static void unzip(ZipInputStream zipInput, String destinationFolder) {
        Path directory = Paths.get(destinationFolder);

        // buffer for read and write data to file
        byte[] buffer = new byte[2048];

        try {
            Files.createDirectories(directory);
            ZipEntry entry = zipInput.getNextEntry();

            while (entry != null) {
                String entryName = entry.getName();
                Path path = Paths.get(destinationFolder, entryName);
                // create the directories of the zip directory
                if (entry.isDirectory()) {
                    Path newDir = Paths.get(path.toAbsolutePath().toString());
                    Files.createDirectories(newDir);
                } else {
                    if (!Files.exists(path.getParent())) {
                        Files.createDirectories(path.getParent());
                    }
                    OutputStream output = Files.newOutputStream(path);
                    int count = 0;
                    while ((count = zipInput.read(buffer)) > 0) {
                        // write 'count' bytes to the file output stream
                        output.write(buffer, 0, count);
                    }
                    output.close();
                }
                // close ZipEntry and take the next one
                zipInput.closeEntry();
                entry = zipInput.getNextEntry();
            }

            // close the last ZipEntry
            zipInput.closeEntry();

            zipInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String attachJsPrefix(String original, String prefix) {
        return "var " + prefix + " = " + original;
    }

}
