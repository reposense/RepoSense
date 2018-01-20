package frontend;

import dataObject.RepoConfiguration;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import report.RepoInfoFileGenerator;
import system.CSVConfigurationParser;
import system.Console;

import java.io.File;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Created by matanghao1 on 11/7/17.
 */
public class GitGrader extends Application {

    File configFile;
    File targetFile;

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
        grid.setPadding(new Insets(10, 10, 25, 10));

        FileChooser configFileChooser = new FileChooser();
        configFileChooser.setTitle("Open CSV");

        Label configFileLabel = new Label("Config CSV:");
        grid.add(configFileLabel, 0, 1);

        TextField configFileText = new TextField("");
        grid.add(configFileText, 1, 1);

        final Button openConfigFileButton = new Button("Open...");

        grid.add(openConfigFileButton,2,1);


        DirectoryChooser targetChooser = new DirectoryChooser();
        targetChooser.setTitle("Open CSV");

        Label targetFileLabel = new Label("Target Location:");
        grid.add(targetFileLabel, 0, 2);

        TextField targetFileText = new TextField("");
        grid.add(targetFileText, 1, 2);

        final Button targetConfigFileButton = new Button("Open...");

        grid.add(targetConfigFileButton,2,2);

        Label fromDateLabel = new Label("From Date:");
        grid.add(fromDateLabel, 0, 3);

        final DatePicker fromDatePicker = new DatePicker();
        grid.add(fromDatePicker, 1,3);

        Label toDateLabel = new Label("To Date:");
        grid.add(toDateLabel, 0, 4);

        final DatePicker toDatePicker = new DatePicker();
        grid.add(toDatePicker, 1,4);

        final Button startButton = new Button("Start Analysis!");

        grid.add(startButton,1,5);

        TextArea consoleText = TextAreaBuilder.create()
                .prefWidth(300)
                .prefHeight(500)
                .wrapText(true)
                .editable(false)
                .build();
        grid.add(consoleText, 1, 6);

        Console console = new Console(consoleText);
        PrintStream ps = new PrintStream(console, true);
        System.setOut(ps);

        openConfigFileButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        configFile = configFileChooser.showOpenDialog(primaryStage);
                        configFileText.appendText(configFile.getAbsolutePath());
                    }

                });
        targetConfigFileButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        targetFile = targetChooser.showDialog(primaryStage);
                        targetFileText.appendText(targetFile.getAbsolutePath());
                    }
                }
        );

        startButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        Task<Integer> task = new Task<Integer>() {
                            @Override protected Integer call() throws Exception {
                                Date fromDate = null;
                                Date toDate = null;
                                console.clear();
                                if (configFile != null) {
                                    try {
                                        if (fromDatePicker.getValue() != null){
                                            fromDate = convertToDate(fromDatePicker.getValue());
                                        }
                                        if (toDatePicker.getValue() != null){
                                            toDate = convertToDate(toDatePicker.getValue());
                                        }
                                        List<RepoConfiguration> configs = CSVConfigurationParser.parseFromFile(configFile);
                                        for (RepoConfiguration config : configs){
                                            config.setToDate(toDate);
                                            config.setFromDate(fromDate);
                                        }
                                        RepoInfoFileGenerator.generateReposReport(configs, targetFile.getAbsolutePath());
                                    } catch (Exception e){
                                        System.out.println("error caught!!");
                                        e.printStackTrace();
                                    }
                                }
                                return 0;
                            }
                        };
                        new Thread(task).start();
                    }
                }
        );

        Scene scene = new Scene(grid, 550, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Date convertToDate(LocalDate localDate){
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }
}
