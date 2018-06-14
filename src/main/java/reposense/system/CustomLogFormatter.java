package reposense.system;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 * Custom log formatter for different handlers to display only relevant information
 */
public class CustomLogFormatter extends SimpleFormatter {

    private static final DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");

    @Override
    public synchronized String format(LogRecord record) {
        StringBuilder builder = new StringBuilder();
        builder.append(dateFormat.format(new Date(record.getMillis()))).append(" - ");

        Throwable t = record.getThrown();

        if (t != null) {
            builder.append("An error has occurred");
            builder.append("\n");
        } else {
            builder.append(formatMessage(record));
            builder.append("\n");
        }

        return builder.toString();
    }
}
