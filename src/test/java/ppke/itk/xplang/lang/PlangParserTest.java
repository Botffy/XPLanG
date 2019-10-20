package ppke.itk.xplang.lang;

import org.junit.Before;
import org.junit.Test;
import ppke.itk.xplang.ast.Conditional;
import ppke.itk.xplang.ast.Root;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.type.Archetype;

import java.io.Reader;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Testing the small parser objects of PlangGrammar.
 */
public class PlangParserTest {
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
        SequenceParser.parse(parser, Symbol.END_PROGRAM);
        assertEquals(1, parser.getErrorLog().getErrorMessages().size());
    }

    @Test public void conditionalNodeWithoutElseBranch() throws ParseError {
        Reader source = new StringReader("Ha igaz akkor\na:=5\nha_vége");
        parser.parse(source, grammar);
        parser.context().declareVariable(new PlangName("a"), parser.actual(), Archetype.INTEGER_TYPE);

        Conditional cond = ConditionalParser.parse(parser);
        assertFalse("Conditional.children should not contain nulls", cond.getChildren().contains(null));
    }
}
