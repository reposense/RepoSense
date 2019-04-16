package reposense.parser;

import java.nio.file.Path;
import java.nio.file.Paths;

import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.ArgumentType;

/**
 * Checks the argument of {@code --view} flag.
 */
public class ReportFolderArgumentType implements ArgumentType<Path> {
    @Override
    public Path convert(ArgumentParser parser, Argument arg, String value) throws ArgumentParserException {
        // Piggyback on library methods to do file existence checks
        Arguments.fileType().verifyExists().verifyIsDirectory().verifyCanRead().convert(parser, arg, value);
        return Paths.get(value);
    }
}
