package reposense.parser;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.logging.Logger;

import reposense.system.LogsManager;

/**
 * Represents a YAML parser that is able to parse a YAML file from a {@link Path} into an object of
 * type {@code T}.
 *
 * @param <T> Type {@code T} that this parser can parse and return as an object
 */
public abstract class YamlParser<T> {
    protected static final Logger logger = LogsManager.getLogger(YamlParser.class);

    /**
     * Returns the type of {@code T} for YAML file conversion.
     */
    public abstract Type getType();

    /**
     * Converts the YAML file from the given {@code path} into object of type {@code T} and return it.
     *
     * @param path Path to the YAML file
     * @return Parsed object of type {@code T}
     * @throws IOException if the {@code path} is invalid
     */
    public abstract T parse(Path path) throws IOException;
}
