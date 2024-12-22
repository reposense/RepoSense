package reposense.parser.exceptions;

/**
 * Represents the error thrown when Markdown files cannot be parsed.
 */
public class InvalidMarkdownException extends Exception {
    public InvalidMarkdownException(String message) {
        super(message);
    }
}
