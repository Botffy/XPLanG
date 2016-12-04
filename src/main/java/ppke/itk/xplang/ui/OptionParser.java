package ppke.itk.xplang.ui;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;

class OptionParser {
    private static final Logger log = LoggerFactory.getLogger("Root.UI.ArgumentParser");

    private final ArgumentParser parser;

    OptionParser() {
        parser = ArgumentParsers.newArgumentParser("xplang", true)
            .version(String.format("This is XPLanG version %s", Program.getVersion().describe()))
            .defaultHelp(true);

        parser.addArgument("source")
            .metavar("<source-file>")
            .type(Arguments.fileType().acceptSystemIn().verifyCanRead().verifyIsFile())
            .nargs(1)
            .help("Source file");

        parser.addArgument("--version")
            .action(Arguments.version()); // FIXME this exists with a System.exit(0). That's sort of not ideal.
    }

    RunConfig parseOptions(String[] args) {
        log.debug("Parsing command line arguments: {}", Arrays.asList(args));

        try {
            Namespace res = parser.parseArgs(args);
            List<File> files = res.get("source");
            return new RunConfig(Program.Action.getDefaultAction(), files.get(0));
        } catch(ArgumentParserException e) {
            log.error("Argument error: {}", e.getMessage());
            parser.handleError(e);
            return new RunConfig(Program.Action.NONE, null);
        }
    }
}
