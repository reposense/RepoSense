package reposense.model;

import java.lang.reflect.Type;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Represents a file type for use in {@link FileTypeManager}.
 */
public class FileType {
    private static final String FILE_TYPE_VALIDATION_REGEX = "[A-Za-z0-9]+";
    private static final String MESSAGE_ILLEGAL_FILE_TYPE = "The provided file type, %s, contains illegal characters.";

    private String label;
    private List<String> paths;
    private PathMatcher pathsGlob;

    public FileType(String label, List<String> paths) {
        validateFileType(label);
        this.label = label;
        setPaths(paths);
    }

    /**
     * Ensures that the string {@code label} is a valid file type.
     * @throws IllegalArgumentException if {@code label} does not meet the criteria.
     */
    public static void validateFileType(String label) {
        if (!label.matches(FILE_TYPE_VALIDATION_REGEX)) {
            throw new IllegalArgumentException(String.format(MESSAGE_ILLEGAL_FILE_TYPE, label));
        }
    }

    public static List<FileType> convertFormatStringsToFileTypes(List<String> formats) {
        return formats.stream().map(FileType::convertStringFormatToFileType).collect(Collectors.toList());
    }

    public static FileType convertStringFormatToFileType(String format) {
        return new FileType(format, Collections.singletonList("**" + format));
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

