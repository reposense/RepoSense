package reposense.parser;

import java.nio.file.Path;
import java.nio.file.Paths;

import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.ArgumentType;

/**
 * Checks the argument of {@code --output} flag.
 */
public class OutputFolderArgumentType implements ArgumentType<Path> {
    @Override
    public Path convert(ArgumentParser parser, Argument arg, String value) throws ArgumentParserException {
        // Piggyback on library methods to do file existence checks
        Arguments.fileType().verifyExists().verifyIsDirectory().verifyCanWrite()
                .or()
                .verifyNotExists().convert(parser, arg, value);
        return Paths.get(value).resolve(ArgsParser.DEFAULT_REPORT_NAME);
    }
}
