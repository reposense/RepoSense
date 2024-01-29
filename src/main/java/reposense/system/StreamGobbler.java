package reposense.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Contains input stream consumption and processing related functionalities.
 */
public class StreamGobbler extends Thread {

    private final ByteBuffer buffer = ByteBuffer.allocate(1 << 13); // 8KB

    private InputStream is;
    private String value;

    public StreamGobbler(InputStream is) {
        this.is = is;
    }

    public String getValue() {
        return value;
    }

    /**
     * Reads from input stream {@code is} and stores it into {@code value}.
     */
    @Override
    public void run() {
        try {
            StringBuilder sb = new StringBuilder();

//            try (BufferedReader streamReader = new BufferedReader(
//                    new InputStreamReader(is)
//            )) {
//                int c;
//
//                while ((c = streamReader.read()) != -1) {
//                    sb.append((char) c);
//                }
//
//                value = sb.toString();
//            }

            ReadableByteChannel ch = Channels.newChannel(is);
            int len;
            while ((len = ch.read(buffer)) > 0) {
                sb.append(new String(buffer.array(), 0, len));
                buffer.rewind();
            }
            value = sb.toString();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
