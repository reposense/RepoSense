package reposense.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;

import reposense.parser.GenericInput;
import reposense.parser.GenericInputType;

/**
 * Represents command line arguments user supplied when running the program.
 */
public class CliArguments {
    private Optional<GenericInput> genericInput;
    private Optional<Date> sinceDate;
    private Optional<Date> untilDate;
    private Path outputPath;

    public CliArguments(Optional<GenericInput> genericInput) {
        this(genericInput, Optional.empty(), Optional.empty(), Paths.get("."));
    }

    public CliArguments(Optional<GenericInput> genericInput,
            Optional<Date> sinceDate, Optional<Date> untilDate, Path outputPath) {
        this.genericInput = genericInput;
        this.sinceDate = sinceDate;
        this.untilDate = untilDate;
        this.outputPath = outputPath;
    }

    public GenericInputType getGenericInputType() {
        return genericInput.get().getType();
    }

    public <T> T getGenericInputValue() {
        if (!genericInput.isPresent()) {
            throw new AssertionError("Wrong usage");
        }

        return genericInput.get().getValue();
    }

    public Path getOutputPath() {
        return outputPath;
    }

    public Optional<Date> getSinceDate() {
        return sinceDate;
    }

    public Optional<Date> getUntilDate() {
        return untilDate;
    }

}
