package frontend;

import builder.ConfigurationBuilder;
import dataObject.Configuration;
import javafx.application.Application;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import report.RepoInfoFileGenerator;
import system.Console;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by matanghao1 on 11/7/17.
 */
public class GitGrader extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("GitGrader");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 10, 25, 10));

        Label orgLabel = new Label("Organization:");
        grid.add(orgLabel, 0, 1);

        TextField orgText = new TextField("se-edu");
        grid.add(orgText, 1, 1);

        Label repoLabel = new Label("Repo Name:");
        grid.add(repoLabel, 0, 2);

        TextField repoText = new TextField("addressbook-level4");
        grid.add(repoText, 1, 2);

        Label branchLabel = new Label("Branch Name:");
        grid.add(branchLabel, 0, 3);

        TextField branchText = new TextField("master");
        grid.add(branchText, 1, 3);

        Label numCommitLabel = new Label("How many Commits:");
        grid.add(numCommitLabel, 0, 4);

        TextField numCommitText = new TextField("5");
        grid.add(numCommitText, 1, 4);

        Label ignoreListLabel = new Label("ignore list:");
        grid.add(ignoreListLabel, 0, 5);

        TextArea ignoreListText  = TextAreaBuilder.create()
                .prefWidth(300)
                .prefHeight(100)
                .wrapText(true)
                .promptText("(one entry each line)")
                .build();
        grid.add(ignoreListText, 1, 5);


        Label qualityCheckLabel = new Label("Quality Check:");
        grid.add(qualityCheckLabel, 0, 6);


        CheckBox checkStyleCb = new CheckBox("CheckStyle");
        grid.add(checkStyleCb, 1, 6, 2, 1);


        CheckBox annotationCb = new CheckBox("Annotation Overwrite");
        grid.add(annotationCb, 1, 7, 2, 1);



        Button btn = new Button("Analyze");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 8);


        TextArea consoleText = TextAreaBuilder.create()
                .prefWidth(300)
                .prefHeight(200)
                .wrapText(true)
                .editable(false)
                .build();
        grid.add(consoleText, 1, 9);

        Console console = new Console(consoleText);
        PrintStream ps = new PrintStream(console, true);
        System.setOut(ps);


        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                Task<Integer> task = new Task<Integer>() {
                    @Override protected Integer call() throws Exception {
                        String org = orgText.getText();
                        String repoName = repoText.getText();
                        String branch = branchText.getText();
                        console.clear();

                        Configuration config = new ConfigurationBuilder(org,repoName,branch)
                                .needCheckStyle(checkStyleCb.isSelected())
                                .annotationOverwrite(annotationCb.isSelected())
                                .commitNum(Integer.parseInt(numCommitText.getText()))
                                .ignoreList(getIgnoreListFromRaw(ignoreListText.getText()))
                                .build();

                        RepoInfoFileGenerator.generateReport(config);
                        return 0;
                    }
                };
                new Thread(task).start();
            }
        });

        Scene scene = new Scene(grid, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private List<String> getIgnoreListFromRaw(String raw){
        if ("".equals(raw)){
            return new ArrayList<>();
        }else{
            return Arrays.asList(raw.split("\n"));
        }
    }
}
