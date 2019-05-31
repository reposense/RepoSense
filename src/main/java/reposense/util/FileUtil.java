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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import reposense.model.RepoConfiguration;
import reposense.system.LogsManager;

/**
 * Contains file processing related functionalities.
 */
public class FileUtil {
    public static final String REPOS_ADDRESS = "repos";

    // zip file which contains all the specified file types
    public static final String ZIP_FILE = "archive.zip";

    private static final Logger logger = LogsManager.getLogger(FileUtil.class);
    private static final String GITHUB_API_DATE_FORMAT = "yyyy-MM-dd";
    private static final ByteBuffer buffer = ByteBuffer.allocate(1 << 11); // 2KB

    /**
     * Returns a list of relevant repo folders to be zipped up later
     * @param sourcePath contains the source directory where the repo folders are located at
     * @param configs contains the list of configuration information for a single repository
     * @return a set of relevant repo folders (to be zipped later)
     */
    public static HashSet<Path> getReportFolders(Path sourcePath, List<RepoConfiguration> configs) {
        HashSet<Path> relevantFolders = new HashSet<>();
        for (RepoConfiguration repoConfiguration : configs) {
            relevantFolders.add(Paths.get(sourcePath + File.separator + repoConfiguration.getDisplayName())
                    .toAbsolutePath());
        }
        return relevantFolders;
    }

    /**
     * Zips all the relative folders and relevant files of {@code fileTypes} contained in
     * {@code pathsToZip} directory into {@code sourceAndOutputPath}.
     * @param pathsToZip contains the folders and files to be zipped.
     * @param sourceAndOutputPath contains the directory where the source folders and files are located at and
     *                           where to be zipped to.
     * @param fileTypes contains the file types to be zipped. Only files which are of the type "fileTypes" will be
     *                  zipped.
     */
    public static void zipFoldersAndFiles(HashSet<Path> pathsToZip,
            Path sourceAndOutputPath, String... fileTypes) {
        zipFoldersAndFiles(pathsToZip, sourceAndOutputPath, sourceAndOutputPath, fileTypes);
    }

    /**
     * Zips all the relevant files and relative folders
     * @param pathsToZip contains the relevant folders to be zipped.
     * @param sourcePath contains the directory where the relevant folders and files are located at
     * @param outputPath contains the directory to be zipped to.
     * @param fileTypes contains the file types to be zipped. Only files which are of the type "fileTypes" will be
     *                  zipped.
     */
    public static void zipFoldersAndFiles(HashSet<Path> pathsToZip,
            Path sourcePath, Path outputPath, String... fileTypes) {
        try (
                FileOutputStream fos = new FileOutputStream(outputPath + File.separator + ZIP_FILE);
                ZipOutputStream zos = new ZipOutputStream(fos)
        ) {
            for (Path pathToZip : pathsToZip) {
                Set<Path> allPaths = getFilePaths(pathToZip, fileTypes);
                for (Path path : allPaths) {
                    String filePath = sourcePath.relativize(path.toAbsolutePath()).toString();
                    String zipEntry = Files.isDirectory(path) ? filePath + File.separator : filePath;
                    zos.putNextEntry(new ZipEntry(zipEntry.replace("\\", "/")));
                    if (Files.isRegularFile(pathToZip)) {
                        Files.copy(path, zos);
                    }
                    zos.closeEntry();
                }
            }
        } catch (IOException ioe) {
            logger.log(Level.SEVERE, ioe.getMessage(), ioe);
        }
    }

    /**
     * Writes the JSON file representing the {@code object} at the given {@code path}.
     */
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

    /**
     * Deletes the {@code root} directory.
     * @throws IOException if the root path does not exist.
     */
    public static void deleteDirectory(String root) throws IOException {
        File rootDirectory = Paths.get(root).toFile();
        if (rootDirectory.exists()) {
            for (File file : rootDirectory.listFiles()) {
                if (file.isDirectory()) {
                    deleteDirectory(file.toString());
                } else {
                    file.delete();
                }
            }
            rootDirectory.delete();
            if (rootDirectory.exists()) {
                throw new IOException(String.format("Fail to delete directory %s", rootDirectory));
            }
        }
    }

    /**
     * Unzips the contents of the {@code zipSourcePath} into {@code outputPath}.
     * @throws IOException if {@code zipSourcePath} is an invalid path.
     */
    public static void unzip(Path zipSourcePath, Path outputPath) throws IOException {
        try (InputStream is = Files.newInputStream(zipSourcePath)) {
            unzip(is, outputPath);
        }
    }

    /**
     * Unzips the contents of the {@code is} into {@code outputPath}.
     * @throws IOException if {@code is} refers to an invalid path.
     */
    public static void unzip(InputStream is, Path outputPath) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
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
        }
    }

    /**
     * Copies the template files from {@code sourcePath} to the {@code outputPath}.
     * @throws IOException if {@code is} refers to an invalid path.
     */
    public static void copyTemplate(InputStream is, String outputPath) throws IOException {
        FileUtil.unzip(is, Paths.get(outputPath));
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
    private static Set<Path> getFilePaths(Path directoryPath, String... fileTypes) throws IOException {
        return Files.walk(directoryPath)
                .filter(p -> FileUtil.isFileTypeInPath(p, fileTypes) || Files.isDirectory(p))
                .collect(Collectors.toSet());
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
