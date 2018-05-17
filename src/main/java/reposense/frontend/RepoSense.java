package reposense.frontend;

import java.io.File;
import java.util.Date;
import java.util.List;

import reposense.configparser.CliArgumentsParser;

import reposense.configparser.InputParameter;

import reposense.dataobject.RepoConfiguration;
import reposense.exceptions.ParseException;
import reposense.report.RepoInfoFileGenerator;
import reposense.system.CsvConfigurationBuilder;


public class RepoSense {

    public static final int FLAG_SUCCESS = 0;
    public static final int FLAG_ERROR = 1;

    public static void main(String[] args) {
        if (args.length == 0) {
            showHelpMessage();
        }

        try {
            CliArgumentsParser cliArgumentsParser = new CliArgumentsParser();
            InputParameter argument = cliArgumentsParser.parse(args);

            File configFile = argument.getConfigFile();
            File targetFile = argument.getTargetFile();
            Date fromDate = argument.getSinceDate().orElse(null);
            Date toDate = argument.getUntilDate().orElse(null);

            List<RepoConfiguration> configs = CsvConfigurationBuilder.buildConfigs(configFile, fromDate, toDate);
            RepoInfoFileGenerator.generateReposReport(configs, targetFile.getAbsolutePath());
        } catch (ParseException exception) {
            showHelpMessage();
            System.out.print(exception.getMessage());
        }
    }

    private static void showHelpMessage() {
        System.out.println("usage: java -jar RepoSense.jar -config CSV_CONFIG_FILE_PATH\n"
                + "   [-output OUTPUT_DIRECTORY]\n"
                + "   [-since DD/MM/YYYY]\n"
                + "   [-until DD/MM/YYYY]\n");
        System.out.println("-config: Mandatory. The path to the CSV config file.");
        System.out.println("-output: Optional. The path to the dashboard generated.\n"
                + "   If not provided, it will be generated in the current directory.");
        System.out.println("-since : Optional. start date of analysis. Format: dd/MM/yyyy");
        System.out.println("-until : Optional. end date of analysis. Format: dd/MM/yyyy");
    }
}
