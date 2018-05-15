package reposense.frontend;


import reposense.ConfigParser.Argument;
import reposense.ConfigParser.CliArgumentsParser;
import reposense.ConfigParser.ConfigParser;
import reposense.ConfigParser.Parser;
import reposense.dataobject.RepoConfiguration;
import reposense.exceptions.ParseException;
import reposense.report.RepoInfoFileGenerator;
import sun.security.krb5.Config;

import java.util.List;

public class RepoSense {

    public static final int FLAG_SUCCESS = 0;
    public static final int FLAG_ERROR = 1;

    public static void main(String[] args) {
        if (args.length == 0) {
            showHelpMessage();
        }

        try {
            CliArgumentsParser cliArgumentsParser = new CliArgumentsParser();
            Argument argument = cliArgumentsParser.parse(args);

            ConfigParser configParser = new ConfigParser();
            List<RepoConfiguration> configs = configParser.parse(argument);

            RepoInfoFileGenerator.generateReposReport(configs, argument.getTargetFile().getAbsolutePath());
        } catch (ParseException exception) {
            showHelpMessage();
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
