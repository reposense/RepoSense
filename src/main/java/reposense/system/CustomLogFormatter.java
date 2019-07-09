package reposense.system;

import static org.fusesource.jansi.Ansi.ansi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    public CustomLogFormatter() {
        AnsiConsole.systemInstall();
    }

    @Override
    public synchronized String format(LogRecord record) {
        StringBuilder builder = new StringBuilder();
        builder.append(dateFormat.format(new Date(record.getMillis()))).append(" - ");

        if (record.getLevel().equals(Level.SEVERE)) {
            builder.append(ERROR_HIGHLIGHT).append(" ");
        } else if (record.getLevel().equals(Level.WARNING)) {
            builder.append(WARNING_HIGHLIGHT).append(" ");
        }

        builder.append(formatMessage(record));
        builder.append("\n");

        return builder.toString();
    }
}
