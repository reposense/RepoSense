package frontend;

import dataObject.RepoConfiguration;
import dataObject.RepoInfo;
import javafx.application.Application;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;

import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import report.RepoInfoFileGenerator;
import system.CSVConfigurationParser;
import system.Console;

import java.io.File;
import java.io.PrintStream;
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
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 10, 25, 10));

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open CSV");

        final Button openButton = new Button("Open a csv...");

        grid.add(openButton,1,1);


        TextArea consoleText = TextAreaBuilder.create()
                .prefWidth(300)
                .prefHeight(400)
                .wrapText(true)
                .editable(false)
                .build();
        grid.add(consoleText, 1, 3);

        Console console = new Console(consoleText);
        PrintStream ps = new PrintStream(console, true);
        System.setOut(ps);

        openButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        final File file = fileChooser.showOpenDialog(primaryStage);

                        Task<Integer> task = new Task<Integer>() {
                            @Override protected Integer call() throws Exception {
                                console.clear();
                                if (file != null) {
                                    List<RepoConfiguration> configs = CSVConfigurationParser.parseFromFile(file);
                                    RepoInfoFileGenerator.generateReposReport(configs);
                                }
                                return 0;
                            }
                        };
                        new Thread(task).start();
                    }

                });


        Scene scene = new Scene(grid, 400, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
