package ppke.itk.xplang.parser;

import org.junit.Test;
import ppke.itk.xplang.ast.Root;
import ppke.itk.xplang.common.CompilerMessage;
import ppke.itk.xplang.common.CursorPosition;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ParserErrorLoggingTest {
    private final static Symbol SYMBOL_FSR = new Symbol("FUNNY_STARE_RIGHT", Pattern.compile(">_>"));
    private final static Symbol SYMBOL_FSL = new Symbol("FUNNY_STARE_LEFT",  Pattern.compile("<_<"));
    private final static Symbol SYMBOL_WS = new Symbol("WS",  Pattern.compile("\\s+"), Symbol.Precedence.DEFAULT, false);
    private final static Grammar grammar = new Grammar() {
        @Override protected void setup(Context ctx) {
            ctx.register(SYMBOL_FSR);
            ctx.register(SYMBOL_FSL);
            ctx.register(SYMBOL_WS);
        }
        @Override protected Root start(Parser parser) throws ParseError {
            int iteration = 0;
            while(!parser.actual().symbol().equals(Symbol.EOF)) {
                parser.accept(iteration %2 == 0? SYMBOL_FSL : SYMBOL_FSR);
                iteration++;
            }
            return null;
        }
    };

    @Test
    public void parserShouldLogLexerError() {
        Reader source = new StringReader("<_< öö >_>");
        Parser parser = new Parser();
        parser.parse(source, grammar);

        List<CompilerMessage> messages = parser.getErrorLog().getErrorMessages();
        assertFalse("Error log should not be empty after a lexing error.", messages.isEmpty());
        assertEquals("Error log should give correct information about the error location.",
            new CursorPosition(1,5),
            messages.get(0).getCursorPosition()
        );
    }

    @Test
    public void parserShouldLogSyntaxError() {
        Reader source = new StringReader("<_< >_> >_> >_>");
        Parser parser = new Parser();
        parser.parse(source, grammar);

        List<CompilerMessage> messages = parser.getErrorLog().getErrorMessages();
        System.out.println(messages);
        assertFalse("Error log should not be empty after a syntax error.", parser.getErrorLog().isEmpty());
        assertEquals("Error log should give correct information about the error location.",
            new CursorPosition(1,9),
            messages.get(0).getCursorPosition()
        );
    }
}
