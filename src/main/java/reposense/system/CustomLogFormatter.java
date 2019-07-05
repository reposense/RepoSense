package reposense.system;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 * Custom log formatter for different handlers to display only relevant information
 */
public class CustomLogFormatter extends SimpleFormatter {

    private static final DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
    private static final String ERROR_HIGHLIGHT = "\u001b[37;41m[ERROR]\u001b[0m";

    @Override
    public synchronized String format(LogRecord record) {
        StringBuilder builder = new StringBuilder();
        builder.append(dateFormat.format(new Date(record.getMillis()))).append(" - ");

        if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
            builder.append(ERROR_HIGHLIGHT).append(" ");
        }

        builder.append(formatMessage(record));
        builder.append("\n");

        return builder.toString();
    }
}
