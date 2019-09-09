package ppke.itk.xplang.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.Root;
import ppke.itk.xplang.common.ErrorLog;
import ppke.itk.xplang.lang.PlangGrammar;
import ppke.itk.xplang.parser.Grammar;
import ppke.itk.xplang.parser.Parser;

import java.io.Reader;
import java.io.StringReader;

class Compiler {
    private static final Logger log = LoggerFactory.getLogger("Root.Gui.Compiler");

    Compiler() { }

    public Result compile(String programText) {
        Reader source = new StringReader(programText);

        ErrorLog errorLog = new ErrorLog();
        Grammar grammar = new PlangGrammar();
        Parser parser = new Parser(errorLog);

        Root ast = parser.parse(source, grammar);
        log.info("Compiled. AST ready: {}, errorLog: {}", ast != null, errorLog);

        return new Result(errorLog, ast);
    }

    public static class Result {
        private final ErrorLog errorLog;
        private final Root ast;

        Result(ErrorLog errorLog, Root ast) {
            this.errorLog = errorLog;
            this.ast = ast;
        }

        public ErrorLog getErrorLog() {
            return errorLog;
        }

        public Root getAst() {
            return ast;
        }

        public boolean isSuccess() {
            return ast != null && errorLog.hasNoErrors();
        }
    }
}
