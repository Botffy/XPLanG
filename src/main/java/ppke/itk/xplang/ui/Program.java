package ppke.itk.xplang.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.ASTPrinter;
import ppke.itk.xplang.ast.Root;
import ppke.itk.xplang.interpreter.Interpreter;
import ppke.itk.xplang.lang.PlangGrammar;
import ppke.itk.xplang.parser.Grammar;
import ppke.itk.xplang.parser.Parser;
import ppke.itk.xplang.util.VersionInfo;

import java.io.Reader;
import java.io.StringReader;


class Program {
    private final static Logger log = LoggerFactory.getLogger("Root.UI");
    private final static VersionInfo version = new VersionInfo();

    Program() {
        // empty ctor
    }

    /**
     * Run the program.
     */
    void run() {
        log.info("XPLanG starting");
        log.info("OS: {}", System.getProperty("os.name"));
        log.info("Java: {}", System.getProperty("java.version"));
        log.info("Version: {}", version.describe());

        Reader source = new StringReader("PROGRAM testing <_< >_> >_> \n >_> >_> program_vége");

        Grammar grammar = new PlangGrammar();
        Parser parser = new Parser();
        try {
            Root root = parser.parse(source, grammar);

            ASTPrinter printer = new ASTPrinter();
            printer.visit(root);

            Interpreter interpreter = new Interpreter();
            interpreter.visit(root);
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
