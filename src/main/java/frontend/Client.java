package frontend;

import dataObject.RepoConfiguration;
import report.RepoInfoFileGenerator;
import system.CSVConfigurationBuilder;
import util.Constants;

import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by matanghao1 on 25/1/18.
 */
public class Client {
    final static String CONFIG_FILE_ARG = "config";
    final static String OUTPUT_FILE_ARG = "output";
    final static String SINCE_DATE_ARG = "since";
    final static String UNTIL_DATE_ARG = "until";

    private HashMap<String, String> argMap = new HashMap<>();


    public void run(String[] args){
        if (!parseArgs(args)){
            System.out.println("Illegal Arguments");
            return;
        }
        File configFile = null;
        File targetFile = null;
        Date fromDate = null;
        Date toDate = null;
        if (!argMap.containsKey(CONFIG_FILE_ARG)){
            System.out.println("you need to specify a config CSV file!");
            return;
        } else {
            configFile = new File(argMap.get(CONFIG_FILE_ARG));
        }
        if (argMap.containsKey(OUTPUT_FILE_ARG)){
            targetFile = new File(argMap.get(OUTPUT_FILE_ARG));
        } else{
            targetFile = new File(".");
        }
        try {
            fromDate = parseDateArgs(SINCE_DATE_ARG);
            toDate = parseDateArgs(UNTIL_DATE_ARG);
        } catch(ParseException e){
            return;
        }

        List<RepoConfiguration> configs = CSVConfigurationBuilder.buildConfigs(configFile,fromDate, toDate);
        RepoInfoFileGenerator.generateReposReport(configs, targetFile.getAbsolutePath());
    }

    private Date parseDateArgs(String argName) throws ParseException {
        if (argMap.containsKey(argName)){
            String raw =argMap.get(argName);
            try {
                return Constants.CLI_ARGS_DATE_FORMAT.parse(raw);
            } catch (ParseException e) {
                System.out.println("illegal date: "+raw);
                throw e;
            }
        } else{
            return null;
        }
    }

    private boolean parseArgs(String[] args){
        for (int i=0;i<args.length;i=i+2){
            if (i+1>args.length){
                return false;
            }
            if (!args[i].startsWith("--")){
                return false;
            }
            String key=args[i].substring(2);
            argMap.put(key,args[i+1]);
        }
        return true;
    }
}
