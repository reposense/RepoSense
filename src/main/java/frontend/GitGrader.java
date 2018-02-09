package frontend;

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
