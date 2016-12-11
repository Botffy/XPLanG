package ppke.itk.xplang.lang;

import org.junit.Before;
import org.junit.Test;
import ppke.itk.xplang.ast.Root;
import ppke.itk.xplang.parser.Grammar;
import ppke.itk.xplang.parser.LexerError;
import ppke.itk.xplang.parser.Parser;

import java.io.Reader;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class SequenceParserTest {
    private final Grammar grammar = new PlangGrammar() {
        @Override protected Root start(Parser parser) {
            return null;
            // parser's head is still on the first symbol
        }
    };

    private Parser parser;

    @Before
    public void setUp() {
        parser = new Parser();
    }

    /**
     * Sequence should try to recover after errors. If it tries doing it naively, by calling advance() in the catch
     * block, it will generate a lot of errors (because it has to deal with the remainder of the failing statement. So
     * we would like to skip to the next line instead, which we hope will contain the next expression.
     */
    @Test public void skipToNextLineAfterError() throws LexerError {
        Reader source = new StringReader("c:=5\nprogram_vége");
        parser.parse(source, grammar);
        SequenceParser.parse(parser, PlangSymbol.END_PROGRAM.symbol());
        assertEquals(1, parser.getErrorLog().getErrorMessages().size());
    }
}
