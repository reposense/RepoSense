package frontend;

import util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by matanghao1 on 11/7/17.
 */
public class GitGrader {
    public static void main(String[] args){
        if (args.length==0){
            new Thread() {
                @Override
                public void run() {
                    javafx.application.Application.launch(GUI.class);
                }
            }.start();
        }else{
            (new Client()).run(args);
        }
    }
}
