package system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class StreamGobbler extends Thread {

    private InputStream is;
    private String value = "";

    public StreamGobbler(InputStream is) {
        this.is = is;
    }

    public String getValue() {
        return value;
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
