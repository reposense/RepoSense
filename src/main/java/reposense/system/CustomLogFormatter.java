package reposense.system;

import static org.fusesource.jansi.Ansi.ansi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

/**
 * Custom log formatter for different handlers to display only relevant information
 */
public class CustomLogFormatter extends SimpleFormatter {

    private static final DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
    private static final String ERROR_HIGHLIGHT = ansi().bg(Ansi.Color.RED).fg(Ansi.Color.WHITE).a("[ERROR]")
            .reset().toString();
    private static final String WARNING_HIGHLIGHT = ansi().bg(Ansi.Color.YELLOW).fg(Ansi.Color.BLACK).a("[WARNING]")
            .reset().toString();
    private static final Map<Level, String> formatMap = new HashMap<>();

    public CustomLogFormatter() {
        formatMap.put(Level.WARNING, WARNING_HIGHLIGHT);
        formatMap.put(Level.SEVERE, ERROR_HIGHLIGHT);
        AnsiConsole.systemInstall();
    }

    /**
     * Returns the string representation of the {@code record} with
     * the timestamp in hh:mm:ss and the record severity level if applicable.
     */
    @Override
    public synchronized String format(LogRecord record) {
        StringBuilder builder = new StringBuilder();
        builder.append(dateFormat.format(new Date(record.getMillis()))).append(" - ");

        if (formatMap.containsKey(record.getLevel())) {
            builder.append(formatMap.get(record.getLevel())).append(" ");
        }

        builder.append(formatMessage(record));
        builder.append("\n");

        return builder.toString();
    }
}
