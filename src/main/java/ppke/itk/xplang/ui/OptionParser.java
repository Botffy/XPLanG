package ppke.itk.xplang.ui;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

class OptionParser {
    private static final Logger log = LoggerFactory.getLogger("Root.UI.ArgumentParser");

    private final ArgumentParser parser;

    OptionParser() {
        parser = ArgumentParsers.newFor("xplang")
            .addHelp(false)
            .singleMetavar(true)
            .build()
            .version(String.format("This is XPLanG version %s", Program.getVersion().describe()))
            .defaultHelp(true);

        parser.addArgument("--gui")
            .required(false)
            .action(Arguments.storeTrue())
            .help("Start the editor");

        parser.addArgument("source")
            .metavar("<source-file>")
            .type(Arguments.fileType().acceptSystemIn().verifyCanRead().verifyIsFile())
            .nargs(1)
            .help("Source file.");

        parser.addArgument("--source-encoding")
            .type(Program.Encoding.class)
            .setDefault(Program.Encoding.UTF8)
            .help("The encoding of the source code.");

        parser.addArgument("--output-encoding")
            .type(Program.Encoding.class)
            .help("The encoding of the standard output. When not set, the system default will be used.");

        parser.addArgument("-d", "--dry-run")
            .action(Arguments.storeTrue())
            .help("Perform a dry run: parse and analyse the source, displaying any errors, but do not interpret it.");

        parser.addArgument("--print-ast")
            .action(Arguments.storeTrue())
            .help("Print the Abstract Syntax Tree after parsing the program");

        parser.addArgument("--dump-memory")
            .action(Arguments.storeTrue())
            .help("Dump the contents of the memory to the StdOut after running the program.");

        parser.addArgument("-h", "-?", "--help")
            .action(new Helplike(ParserInterrupt.Type.HELP))
            .help("Display this help and exit.");

        parser.addArgument("--version")
            .action(new Helplike(ParserInterrupt.Type.VERSION))
            .help("Display version information and exit.");
    }

    RunConfig parseOptions(String[] args) {
        log.debug("Parsing command line arguments: {}", Arrays.asList(args));

        try {
            Namespace res = parser.parseArgs(args);
            List<File> files = res.get("source");

            Program.Action action = Program.Action.INTERPRET;
            if (res.get("dry_run")) {
                action = Program.Action.PARSE_ONLY;
            }

            if (res.getBoolean("gui")) {
                action = Program.Action.SHOW_GUI;
            }

            RunConfig run = new RunConfig(action);

            run.shouldPrintAst(res.get("print_ast"));
            run.shouldDumpMemory(res.get("dump_memory"));
            run.setOutputEncoding(res.get("output_encoding"));
            run.setSourceEncoding(res.get("source_encoding"));
            run.setSourceFile(files.get(0));

            return run;
        } catch(ParserInterrupt interrupt) {
            switch(interrupt.type) {
                case HELP:
                    System.out.println(parser.formatHelp());
                    break;
                case VERSION:
                    System.out.println(parser.formatVersion());
                    break;
            }
            return new RunConfig(Program.Action.NONE);
        } catch(ArgumentParserException e) {
            log.error("Argument error: {}", e.getMessage());
            parser.handleError(e);
            return new RunConfig(Program.Action.NONE);
        }
    }

    private static class ParserInterrupt extends RuntimeException {
        private enum Type {
            HELP,
            VERSION
        }

        private final Type type;
        ParserInterrupt(Type type) {
            super(type.name());
            this.type = type;
        }
    }

    private static class Helplike implements ArgumentAction {
        private final ParserInterrupt.Type type;
        private Helplike(ParserInterrupt.Type type) {
            this.type = type;
        }
        @Override public void run(ArgumentParser parser, Argument arg, Map<String, Object> attrs, String flag, Object value) {
            throw new ParserInterrupt(this.type);
        }
        @Override public void onAttach(Argument arg) {}
        @Override public boolean consumeArgument() {
            return false;
        }
    }
}
