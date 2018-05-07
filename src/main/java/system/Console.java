package system;

import java.io.IOException;
import java.io.OutputStream;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class Console extends OutputStream {
    private TextArea output;

    public Console(TextArea ta) {
        this.output = ta;
    }

    @Override
    public void write(int i) throws IOException {
        Platform.runLater(new Runnable() {
            public void run() {
                if (output.getText().length() > 5000) {
                    output.clear();
                }
                output.appendText(String.valueOf((char) i));
            }
        });
    }

    public void clear() {
        output.clear();
    }

}
