package ppke.itk.xplang.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.ASTPrinter;
import ppke.itk.xplang.ast.Root;
import ppke.itk.xplang.common.CompilerMessage;
import ppke.itk.xplang.common.ErrorLog;
import ppke.itk.xplang.common.StreamHandler;
import ppke.itk.xplang.interpreter.Interpreter;
import ppke.itk.xplang.lang.PlangGrammar;
import ppke.itk.xplang.parser.Grammar;
import ppke.itk.xplang.parser.Parser;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


class Program {
    private final static Logger log = LoggerFactory.getLogger("Root.UI");
    private final static VersionInfo version = new VersionInfo();

    static VersionInfo getVersion() {
        return version;
    }

    /**
     * Describes the course of action the program should take.
     */
    enum Action {
        /** The program should just call it a day and quit. */
        NONE,

        /** The program should perform a dry-run: analyse the source, but do nothing afterwards. */
        PARSE_ONLY,

        /** The program should take the source code, parse it, build up the AST, then execute it. */
        INTERPRET;
    };

    Program() {
        // empty ctor
    }

    /**
     * Run the program.
     */
    void run(String[] args) {
        log.info("XPLanG starting");
        log.info("OS: {}", System.getProperty("os.name"));
        log.info("Java: {}", System.getProperty("java.version"));
        log.info("Version: {}", version.describe());

        OptionParser optionParser = new OptionParser();
        RunConfig run = optionParser.parseOptions(args);

        if(run.getAction() == Action.NONE) {
            log.info("Exiting");
            return;
        }

        if (run.getOutputEncoding() != null) {
            try {
                System.setOut(new PrintStream(
                    new FileOutputStream(FileDescriptor.out), true,
                    run.getOutputEncoding().getCharset().toString())
                );
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException(e);
            }
        }

        Reader source = getSourceReader(run);

        ErrorLog errorLog = new ErrorLog();
        Grammar grammar = new PlangGrammar();
        Parser parser = new Parser(errorLog);

        Root root = parser.parse(source, grammar);
        if(!errorLog.isEmpty()) {
            printErrors(errorLog);
            return;
        }

        if (run.shouldPrintAst()) {
            ASTPrinter printer = new ASTPrinter();
            printer.visit(root);
        }

        if (run.getAction() == Action.INTERPRET) {
            StreamHandler streamHandler = new FileStreamHandler();
            Interpreter interpreter = new Interpreter(streamHandler);
            interpreter.visit(root);

            if (run.shouldDumpMemory()) {
                System.out.println(interpreter.memoryDump());
            }
        }
    }

    private Reader getSourceReader(RunConfig run) {
        Reader Result;
        try {
            if(run.getSourceFile().getName().equals("-")) {
                Result = new InputStreamReader(System.in);
                log.info("Opened stdin for reading");
            } else {
                Result = new InputStreamReader(new FileInputStream(
                    run.getSourceFile()),
                    run.getSourceEncoding().getCharset()
                );
                log.info("Opened {} for reading", run.getSourceFile());
            }
        } catch(FileNotFoundException e) {
            throw new RuntimeException("Could not open source file for reading.", e);
        }
        return Result;
    }

    private void printErrors(ErrorLog errorLog) {
        for(CompilerMessage message : errorLog.getErrorMessages()) {
            System.out.println(message);
        }
    }

    public enum Encoding {
        LATIN1(StandardCharsets.ISO_8859_1),
        UTF8(StandardCharsets.UTF_8);

        private final Charset charset;

        Encoding(Charset charset) {
            this.charset = charset;
        }

        public Charset getCharset() {
            return charset;
        }
    }
}
