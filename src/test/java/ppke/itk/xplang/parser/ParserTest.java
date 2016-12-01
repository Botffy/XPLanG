package ppke.itk.xplang.parser;


import org.junit.Before;
import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Pattern;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;
import static org.junit.Assert.*;

public class ParserTest {
    private Context ctx;

    @Before public void setUp() {
        ctx = new Context();

        ctx.register(new Symbol("FUNNY_STARE_RIGHT", Pattern.compile(">_>")));
        ctx.register(new Symbol("FUNNY_STARE_LEFT",  Pattern.compile("<_<")));
        ctx.register(new Symbol("SHRUGGIE",  Pattern.compile("¯\\\\_\\(ツ\\)_\\/¯", Pattern.UNICODE_CHARACTER_CLASS)));
        ctx.register(new Symbol("DISAPPROVAL_LOOK",  Pattern.compile("ಠ_ಠ", Pattern.UNICODE_CHARACTER_CLASS)));
        ctx.register(new Symbol("WS",  Pattern.compile("\\s+"), Symbol.Precedence.DEFAULT, false));
    }

    @Test
    public void tapeHeadShouldBeOnFirstToken() throws LexerError {
        Reader source = new StringReader("<_< >_>");
        Parser parser = new Parser(source, this.ctx);
        assertTrue("Actual symbol should be the first symbol after init.",
            parser.actual().getSymbol().equals(ctx.getSymbol("FUNNY_STARE_LEFT"))
        );
    }

    @Test
    public void advanceShouldAdvance() throws LexerError {
        Reader source = new StringReader("<_< >_> ಠ_ಠ ¯\\_(ツ)_/¯");
        Parser parser = new Parser(source, this.ctx);
        parser.advance();
        assertEquals("Actual symbol should be the second symbol after advancing",
            ctx.getSymbol("FUNNY_STARE_RIGHT"), parser.actual().getSymbol()
        );

        parser.advance();
        assertEquals("Actual symbol should be the third symbol after advancing twice",
            ctx.getSymbol("DISAPPROVAL_LOOK"), parser.actual().getSymbol()
        );

        parser.advance();
        assertEquals("Actual symbol should be the fourth symbol after advancing three times",
            ctx.getSymbol("SHRUGGIE"), parser.actual().getSymbol()
        );

        parser.advance();
        assertEquals("Advancing enough should give me an EOF",
            Symbol.EOF, parser.actual().getSymbol()
        );
    }

    @Test
    public void acceptShouldAcceptAndAdvance() throws LexerError {
        Reader source = new StringReader("<_< >_> ಠ_ಠ ¯\\_(ツ)_/¯");
        Parser parser = new Parser(source, this.ctx);

        try {
            Token tok = parser.accept(ctx.getSymbol("FUNNY_STARE_LEFT"), null);
            assertEquals("The accepted symbol should be equal to the expected symbol",
                ctx.getSymbol("FUNNY_STARE_LEFT"), tok.getSymbol()
            );
            assertEquals("Accepting should advance the head",
                ctx.getSymbol("FUNNY_STARE_RIGHT"), parser.actual().getSymbol()
            );
        } catch(Exception e) {
            fail("Accepting should not throw exceptions if accepting the correct symbol");
        }
    }

    @Test
    public void acceptingWrongSymbolShouldCauseError() throws LexerError {
        Reader source = new StringReader("<_< >_> ಠ_ಠ ¯\\_(ツ)_/¯");
        Parser parser = new Parser(source, this.ctx);

        Throwable error = exceptionThrownBy(() -> parser.accept(ctx.getSymbol("SHRUGGIE"), null));
        assertEquals(error.getClass(), SyntaxError.class);
    }


    @Test
    public void lexerErrorShouldBeThrownOnLexerError() throws LexerError, SyntaxError {
        Reader source = new StringReader(">_> öö\n¯\\_(ツ)_/¯");
        Parser parser = new Parser(source, this.ctx);

        Throwable error = exceptionThrownBy(() -> parser.accept(ctx.getSymbol("FUNNY_STARE_RIGHT"), null));
        assertEquals(error.getClass(), LexerError.class);
    }

    @Test
    public void syntaxErrorCanBeCustomised() throws LexerError {
        Reader source = new StringReader(">_>");
        Parser parser = new Parser(source, this.ctx);

        ParseError error = (ParseError) exceptionThrownBy(() ->
            parser.accept(ctx.getSymbol("FUNNY_STARE_LEFT"), "Hi")
        );

        assertEquals("Hi", error.getMessage());
    }

    @Test
    public void parserShouldAcceptItsContext() throws LexerError{
        Reader source = new StringReader(">_>");
        Parser parser = new Parser(source, this.ctx);
        assertSame(this.ctx, parser.context());
    }
}
