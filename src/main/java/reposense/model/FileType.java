package reposense.model;

import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a file type for use in {@link FileTypeManager}.
 */
public class FileType {
    // === NOTE: These variables are just temporary as PRs are being merged in. Need to deprecate them ASAP.
    public static final List<String> DEFAULT_FORMAT_STRINGS = Arrays.asList(
            "adoc", "cs", "css", "fxml", "gradle", "html", "java", "js",
            "json", "jsp", "md", "py", "tag", "txt", "xml");
    public static final List<FileType> DEFAULT_FORMATS = convertStringFormatsToFileTypes(DEFAULT_FORMAT_STRINGS);


    private static final String FILE_TYPE_VALIDATION_REGEX = "[A-Za-z0-9]+";
    private static final String MESSAGE_ILLEGAL_FILE_TYPE = "The provided file type, %s, contains illegal characters.";

    private String label;
    private PathMatcher paths;

    public FileType(String label, List<String> paths) {
        validateFileType(label);
        this.label = label;
        setGroupGlobMatcher(paths);
    }

    /**
     * Checks that the string {@code label} is a valid file type.
     * @throws IllegalArgumentException if {@code label} does not meet the criteria.
     */
    private void validateFileType(String label) {
        if (!label.matches(FILE_TYPE_VALIDATION_REGEX)) {
            throw new IllegalArgumentException(String.format(MESSAGE_ILLEGAL_FILE_TYPE, label));
        }
    }

    public static List<FileType> convertStringFormatsToFileTypes(List<String> formats) {
        return formats.stream().map(FileType::convertStringFormatToFileType).collect(Collectors.toList());
    }

    public static FileType convertStringFormatToFileType(String format) {
        return new FileType(format, Collections.singletonList("**" + format));
    }

    public boolean isFileGlobMatching(String fileName) {
        return getGroupGlobMatcher().matches(Paths.get(fileName));
    }

    private void setGroupGlobMatcher(List<String> filePaths) {
        String globString = "glob:{" + String.join(",", filePaths) + "}";
        this.paths = FileSystems.getDefault().getPathMatcher(globString);
    }

    private PathMatcher getGroupGlobMatcher() {
        return paths;
    }

    @Override
    public String toString() {
        return label;
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (this == other) {
            return true;
        }

        // instanceof handles null
        if (!(other instanceof FileType)) {
            return false;
        }
        FileType otherFileType = (FileType) other;
        return this.label.equals(otherFileType.label);
    }
}
