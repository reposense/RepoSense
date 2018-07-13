package reposense.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import reposense.system.LogsManager;


public class FileUtil {

    // zip file which contains all the specified file types
    public static final String ZIP_FILE = "archive.zip";

    private static final Logger logger = LogsManager.getLogger(FileUtil.class);
    private static final String GITHUB_API_DATE_FORMAT = "yyyy-MM-dd";
    private static final ByteBuffer buffer = ByteBuffer.allocate(1 << 11); // 2KB

    public static void writeJsonFile(Object object, String path) {
        Gson gson = new GsonBuilder()
                .setDateFormat(GITHUB_API_DATE_FORMAT)
                .setPrettyPrinting()
                .create();
        String result = gson.toJson(object);

        try (PrintWriter out = new PrintWriter(path)) {
            out.print(result);
            out.print("\n");
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
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
                    .forEach(filePath -> filePath.toFile().delete());
        }
    }

    /**
     * Zips all the files of {@code fileTypes} contained in {@code sourceAndOutputPath} directory into the same folder.
     */
    public static void zip(Path sourceAndOutputPath, String... fileTypes) {
        FileUtil.zip(sourceAndOutputPath, sourceAndOutputPath, fileTypes);
    }

    /**
     * Zips all the {@code fileTypes} files contained in the {@code sourcePath} and its subdirectories.
     * Creates the zipped {@code ZIP_FILE} file in the {@code outputPath}.
     */
    public static void zip(Path sourcePath, Path outputPath, String... fileTypes) {
        try (
                FileOutputStream fos = new FileOutputStream(outputPath + File.separator + ZIP_FILE);
                ZipOutputStream zos = new ZipOutputStream(fos)
        ) {
            List<Path> allFiles = getFilePaths(sourcePath, fileTypes);
            for (Path path : allFiles) {
                try (InputStream is = Files.newInputStream(path)) {
                    zos.putNextEntry(new ZipEntry(sourcePath.relativize(path.toAbsolutePath()).toString()));
                    int length;
                    while ((length = is.read(buffer.array())) > 0) {
                        zos.write(buffer.array(), 0, length);
                    }
                }
            }
        } catch (IOException ioe) {
            logger.log(Level.SEVERE, ioe.getMessage(), ioe);
        }
    }

    /**
     * Unzips the contents of the {@code zipSourcePath} into {@code outputPath}.
     */
    public static void unzip(Path zipSourcePath, Path outputPath) {
        ZipEntry entry;
        try (
                InputStream is = Files.newInputStream(zipSourcePath);
                ZipInputStream zis = new ZipInputStream(is)
        ) {
            Files.createDirectories(outputPath);
            while ((entry = zis.getNextEntry()) != null) {
                Path path = Paths.get(outputPath.toString(), entry.getName());
                // create the directories of the zip directory
                if (entry.isDirectory()) {
                    Files.createDirectories(path.toAbsolutePath());
                    zis.closeEntry();
                    continue;
                }
                if (!Files.exists(path.getParent())) {
                    Files.createDirectories(path.getParent());
                }
                try (OutputStream output = Files.newOutputStream(path)) {
                    int length;
                    while ((length = zis.read(buffer.array())) > 0) {
                        output.write(buffer.array(), 0, length);
                    }
                }
                zis.closeEntry();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Copies the template files from {@code sourcePath} to the {@code outputPath}.
     */
    public static void copyTemplate(String sourcePath, String outputPath) {
        FileUtil.unzip(Paths.get(sourcePath), Paths.get(outputPath));
    }

    /**
     * Creates the {@code dest} directory if it does not exist.
     */
    public static void createDirectory(Path dest) throws IOException {
        Files.createDirectories(dest);
    }

    /**
     * Returns a list of {@code Path} of {@code fileTypes} contained in the given {@code directoryPath} directory.
     */
    private static List<Path> getFilePaths(Path directoryPath, String... fileTypes) throws IOException {
        return Files.walk(directoryPath)
                .filter(p -> FileUtil.isFileTypeInPath(p, fileTypes))
                .collect(Collectors.toList());
    }

    /**
     * Returns true if the {@code path} contains one of the {@code fileTypes} extension.
     */
    private static boolean isFileTypeInPath(Path path, String... fileTypes) {
        return Arrays.stream(fileTypes).anyMatch(path.toString()::endsWith);
    }

    private static String attachJsPrefix(String original, String prefix) {
        return "var " + prefix + " = " + original;
    }
}
