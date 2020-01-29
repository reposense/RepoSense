package reposense.model;

import java.lang.reflect.Type;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Represents a file type for use in {@link FileTypeManager}.
 */
public class FileType {
    private static final Pattern FILE_FORMAT_VALIDATION_PATTERN = Pattern.compile("^\\w+$");
    private static final String MESSAGE_ILLEGAL_FILE_FORMAT = "The provided file format, %s, contains illegal "
            + "characters.";

    private String label;
    private List<String> paths;
    private PathMatcher pathsGlob;

    public FileType(String label, List<String> paths) {
        validateFileTypeLabel(label);
        this.label = label;
        setPaths(paths);
    }

    public static List<FileType> convertFormatStringsToFileTypes(List<String> formats) {
        return formats.stream().map(FileType::convertStringFormatToFileType).collect(Collectors.toList());
    }

    /**
     * Returns a {@code FileType} with label named {@code format} and globs that include all files that end with
     * {@code format}.
     */
    public static FileType convertStringFormatToFileType(String format) {
        validateFileFormat(format);
        return new FileType(format, Collections.singletonList("**" + format));
    }

    /**
     * Ensures that the string {@code label} is a valid file type.
     * @throws IllegalArgumentException if {@code label} is an empty string.
     */
    public static void validateFileTypeLabel(String label) {
        if (label.isEmpty()) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Ensures that {@code format} is a valid file format.
     * @throws IllegalArgumentException if {@code format} is not alphanumeric.
     */
    public static void validateFileFormat(String format) throws IllegalArgumentException {
        if (!FILE_FORMAT_VALIDATION_PATTERN.matcher(format).matches()) {
            throw new IllegalArgumentException(String.format(MESSAGE_ILLEGAL_FILE_FORMAT, format));
        }
    }

    private void setPaths(List<String> paths) {
        this.paths = paths;
        setPathsGlobMatcher(paths);
    }

    public boolean isFileGlobMatching(String fileName) {
        return pathsGlob.matches(Paths.get(fileName));
    }

    private void setPathsGlobMatcher(List<String> filePaths) {
        String globString = "glob:{" + String.join(",", filePaths) + "}";
        this.pathsGlob = FileSystems.getDefault().getPathMatcher(globString);
    }

    @Override
    public String toString() {
        return label;
    }

    @Override
    public int hashCode() {
        return this.label.hashCode();
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
        return this.label.equals(otherFileType.label) && this.paths.equals(otherFileType.paths);
    }

    /**
     * Overrides the Gson serializer to serialize only the label of each file type instead on the entire object.
     */
    public static class FileTypeSerializer implements JsonSerializer<FileType> {
        @Override
        public JsonElement serialize(FileType fileType, Type typeOfSource, JsonSerializationContext context) {
            return new JsonPrimitive(fileType.toString());
        }
    }
}

