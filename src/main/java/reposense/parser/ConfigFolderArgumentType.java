package reposense.parser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.ArgumentType;

public class ConfigFolderArgumentType  implements ArgumentType<File> {
    private static final String PARSE_EXCEPTION_MESSAGE_MISSING_REQUIRED_CONFIG_FILES =
            "Unable to find " + CsvParser.REPO_CONFIG_FILENAME;
    private static final List<String> REQUIRED_FILES_IN_CONFIG_FOLDER = Arrays.asList(CsvParser.REPO_CONFIG_FILENAME);

    @Override
    public File convert(ArgumentParser parser, Argument arg, String value) throws ArgumentParserException {
        Arguments.fileType().verifyExists().verifyIsDirectory().verifyCanRead().convert(parser, arg, value);
        int matchCount = 0;

        for (String file : REQUIRED_FILES_IN_CONFIG_FOLDER) {
            matchCount += (Files.exists(Paths.get(value, file))) ? 1 : 0;
        }

        if (matchCount != REQUIRED_FILES_IN_CONFIG_FOLDER.size()) {
            throw new ArgumentParserException(
                    String.format(PARSE_EXCEPTION_MESSAGE_MISSING_REQUIRED_CONFIG_FILES, value), parser);
        }

        return new File(value);
    }
}
