package ppke.itk.xplang.parser;


import org.junit.Before;
import org.junit.Test;
import ppke.itk.xplang.ast.Root;

import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Pattern;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;
import static org.junit.Assert.*;

public class ParserTest {
    private final Grammar grammar = new Grammar() {
        @Override protected void setup(Context ctx) {
            ctx.register(new Symbol("FUNNY_STARE_RIGHT", Pattern.compile(">_>")));
            ctx.register(new Symbol("FUNNY_STARE_LEFT",  Pattern.compile("<_<")));
            ctx.register(new Symbol("SHRUGGIE",  Pattern.compile("¯\\\\_\\(-.-\\)_\\/¯", Pattern.UNICODE_CHARACTER_CLASS)));
            ctx.register(new Symbol("DISAPPROVAL_LOOK",  Pattern.compile("ಠ_ಠ", Pattern.UNICODE_CHARACTER_CLASS)));
            ctx.register(new Symbol("WS",  Pattern.compile("\\s+"), Symbol.Precedence.DEFAULT, false));
        }
        @Override protected Root start(Parser parser) throws ParseError {
            return null;
        }
    };

    private Context ctx;

    @Before public void setUp() {
        ctx = new Context();
    }

    @Test
    public void tapeHeadShouldBeOnFirstToken() throws ParseError {
        Reader source = new StringReader("<_< >_>");
        Parser parser = new Parser(this.ctx);
        parser.parse(source, grammar);
        assertTrue("Actual symbol should be the first symbol after init.",
            parser.actual().symbol().equals(ctx.lookup("FUNNY_STARE_LEFT"))
        );
    }

    @Test
    public void advanceShouldAdvance() throws ParseError {
        Reader source = new StringReader("<_< >_> ಠ_ಠ ¯\\_(-.-)_/¯");
        Parser parser = new Parser(this.ctx);
        parser.parse(source, grammar);
        parser.advance();
        assertEquals("Actual symbol should be the second symbol after advancing",
            ctx.lookup("FUNNY_STARE_RIGHT"), parser.actual().symbol()
        );

        parser.advance();
        assertEquals("Actual symbol should be the third symbol after advancing twice",
            ctx.lookup("DISAPPROVAL_LOOK"), parser.actual().symbol()
        );

        parser.advance();
        assertEquals("Actual symbol should be the fourth symbol after advancing three times",
            ctx.lookup("SHRUGGIE"), parser.actual().symbol()
        );

        parser.advance();
        assertEquals("Advancing enough should give me an EOF",
            Symbol.EOF, parser.actual().symbol()
        );
    }

    @Test
    public void acceptShouldAcceptAndAdvance() throws ParseError {
        Reader source = new StringReader("<_< >_> ಠ_ಠ ¯\\_(-.-)_/¯");
        Parser parser = new Parser(this.ctx);
        parser.parse(source, grammar);

        try {
            Token tok = parser.accept(ctx.lookup("FUNNY_STARE_LEFT"), null);
            assertEquals("The accepted symbol should be equal to the expected symbol",
                ctx.lookup("FUNNY_STARE_LEFT"), tok.symbol()
            );
            assertEquals("Accepting should advance the head",
                ctx.lookup("FUNNY_STARE_RIGHT"), parser.actual().symbol()
            );
        } catch(ParseError e) {
            fail("Accepting should not throw exceptions if accepting the correct symbol");
        }
    }

    @Test
    public void acceptingWrongSymbolShouldCauseError() throws ParseError {
        Reader source = new StringReader("<_< >_> ಠ_ಠ ¯\\_(-.-)_/¯");
        Parser parser = new Parser(this.ctx);
        parser.parse(source, grammar);

        Throwable error = exceptionThrownBy(() -> parser.accept(ctx.lookup("SHRUGGIE"), null));
        assertEquals(error.getClass(), SyntaxError.class);
    }


    @Test
    public void lexerErrorShouldBeThrownOnLexerError() throws ParseError {
        Reader source = new StringReader(">_> öö\n¯\\_(-.-)_/¯");
        Parser parser = new Parser(this.ctx);
        parser.parse(source, grammar);

        Throwable error = exceptionThrownBy(() -> parser.accept(ctx.lookup("FUNNY_STARE_RIGHT"), null));
        assertEquals(error.getClass(), LexerError.class);
    }

    @Test
    public void syntaxErrorCanBeCustomised() throws ParseError {
        Reader source = new StringReader(">_>");
        Parser parser = new Parser(this.ctx);
        parser.parse(source, grammar);

        ParseError error = (ParseError) exceptionThrownBy(() ->
            parser.accept(ctx.lookup("FUNNY_STARE_LEFT"), "Hi")
        );

        assertEquals("Hi", error.getMessage());
    }

    @Test
    public void parserShouldAcceptItsContext() throws ParseError {
        Reader source = new StringReader(">_>");
        Parser parser = new Parser(this.ctx);
        parser.parse(source, grammar);
        assertSame(this.ctx, parser.context());
    }
}
