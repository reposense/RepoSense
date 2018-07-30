package reposense.parser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.ArgumentType;

public class ConfigFolderArgumentType implements ArgumentType<Path> {
    private static final String PARSE_EXCEPTION_MESSAGE_MISSING_REQUIRED_CONFIG_FILES =
            String.format("The required config file %s is not found in the specified folder.",
                    CsvParser.REPO_CONFIG_FILENAME);

    @Override
    public Path convert(ArgumentParser parser, Argument arg, String value) throws ArgumentParserException {
        // Piggyback on library methods to do file existence checks
        Arguments.fileType().verifyExists().verifyIsDirectory().verifyCanRead().convert(parser, arg, value);

        if (Files.exists(Paths.get(value).resolve(CsvParser.REPO_CONFIG_FILENAME))) {
            return Paths.get(value);
        }

        throw new ArgumentParserException(
                String.format(PARSE_EXCEPTION_MESSAGE_MISSING_REQUIRED_CONFIG_FILES, value), parser);
    }
}
