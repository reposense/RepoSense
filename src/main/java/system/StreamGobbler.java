package system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by matanghao1 on 28/5/17.
 */
public class StreamGobbler extends Thread {

    InputStream is;

    public String getValue() {
        return value;
    }

    String value ="";

    public StreamGobbler(InputStream is) {
        this.is = is;
    }

    @Override
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                value += line + "\n";
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
