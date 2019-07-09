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
    private static final String CLEAR_REST_OF_SCREEN = ansi().eraseScreen(Ansi.Erase.FORWARD).toString();
    private static final String SAVE_CURSOR_POSITION = ansi().saveCursorPosition().toString();
    private static final String RESTORE_CURSOR_POSITION = ansi().restoreCursorPosition().toString();

    public CustomLogFormatter() {
        AnsiConsole.systemInstall();
    }

    @Override
    public synchronized String format(LogRecord record) {
        StringBuilder builder = new StringBuilder();

        if (record.getLevel().equals(Level.INFO)) {
            builder.append(SAVE_CURSOR_POSITION);
        }

        builder.append(CLEAR_REST_OF_SCREEN);
        builder.append(dateFormat.format(new Date(record.getMillis()))).append(" - ");
        builder.append(formatMessage(record));
        builder.append("\n");

        if (record.getLevel().equals(Level.INFO)) {
            builder.append(RESTORE_CURSOR_POSITION);
        }

        return builder.toString();
    }
}
