package reposense.frontend;

import java.io.IOException;
import java.util.List;

import reposense.dataobject.RepoConfiguration;
import reposense.parser.CsvParser;
import reposense.report.RepoInfoFileGenerator;

public class RepoSense {

    public static void main(String[] args) {
        if (args.length == 0) {
            showHelpMessage();
        }

        try {
            CliArguments cliArguments = new CliArguments(args);
            CsvParser csvParser = new CsvParser();
            List<RepoConfiguration> configs = csvParser.parse(cliArguments);
            RepoInfoFileGenerator.generateReposReport(configs, cliArguments.getTargetFile().getAbsolutePath());
        } catch (IllegalArgumentException iaex) {
            System.out.print(iaex.getMessage());
            showHelpMessage();
        } catch (IOException ioex) {
            System.out.print(ioex.getMessage());
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
