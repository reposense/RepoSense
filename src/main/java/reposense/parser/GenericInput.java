package reposense.parser;

import java.nio.file.Paths;

public class GenericInput {
    public static final String VALID_TYPES_MESSAGE = "csv file or _reposense folder or docs folder";
    private final GenericInputType type;
    private final String value;

    public GenericInput(GenericInputType type, String value) {
        this.type = type;
        this.value = value;
    }

    public GenericInputType getType() {
        return type;
    }

    public <T> T getValue() {
        switch (type) {
        case REPOSENSE_CONFIG_FOLDER:
        case REPOSENSE_REPORT_FOLDER:
        case CSV_FILE:
            return (T) Paths.get(value);
        default:
            throw new AssertionError("Will not hit this line");
        }
    }
}
