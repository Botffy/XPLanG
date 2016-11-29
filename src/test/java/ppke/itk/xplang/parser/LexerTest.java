package ppke.itk.xplang.parser;

import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

public class LexerTest {
    @Test
    public void longestMatch() {
        Symbol keyword = new Symbol("if", Pattern.compile("if"));
        Symbol variable = new Symbol("variable", Pattern.compile("[a-z]+"));
        Symbol whitespace = new Symbol("ws", Pattern.compile("\\s+"));

        List<Symbol> symbols = asList(keyword, variable, whitespace);

        Reader source = new StringReader("iff");
        Lexer lexer = new Lexer(source, symbols);
        assertEquals(
            "Lexer should find the longest match",
            variable, lexer.next().getSymbol()
        );
    }

    @Test public void highestPrecedence() {
        Symbol keyword = new Symbol("if", Pattern.compile("if"), 1);
        Symbol variable = new Symbol("variable", Pattern.compile("[a-z]+"), 0);
        Symbol whitespace = new Symbol("ws", Pattern.compile("\\s+"));

        List<Symbol> symbols = asList(keyword, variable, whitespace);

        Reader source = new StringReader("if");
        Lexer lexer = new Lexer(source, symbols);
        assertEquals(
            "If two matches are of the same length, lexer should hand up the one with the higher precedence",
            keyword, lexer.next().getSymbol()
        );
    }

    @Test public void declarationOrder() {
        Symbol keyword = new Symbol("if", Pattern.compile("if"), 0);
        Symbol variable = new Symbol("variable", Pattern.compile("[a-z]+"), 0);
        Symbol whitespace = new Symbol("ws", Pattern.compile("\\s+"));

        List<Symbol> symbols = asList(keyword, variable, whitespace);

        Reader source = new StringReader("if");
        Lexer lexer = new Lexer(source, symbols);
        assertEquals(
            "If two matches are of the same length and have the same precedence, lexer should hand up the one declared earlier",
            keyword, lexer.next().getSymbol()
        );
    }

    @Test public void eof() {
        Symbol keyword = new Symbol("if", Pattern.compile("if"));

        List<Symbol> symbols = singletonList(keyword);

        Reader source = new StringReader("if");
        Lexer lexer = new Lexer(source, symbols);
        lexer.next();
        assertEquals("Lexer should hand up EOF at the end of the source.", Symbol.EOF, lexer.next().getSymbol());
    }

    @Test public void eol() {
        Symbol keyword = new Symbol("if", Pattern.compile("if"));

        List<Symbol> symbols = singletonList(keyword);

        Reader source = new StringReader("if\nif");
        Lexer lexer = new Lexer(source, symbols);
        lexer.next();
        assertEquals("Lexer should hand up EOL at line end.", Symbol.EOL, lexer.next().getSymbol());
    }

    @Test public void eolAtEof() {
        Symbol keyword = new Symbol("if", Pattern.compile("if"));

        List<Symbol> symbols = singletonList(keyword);

        Reader source = new StringReader("if\n");
        Lexer lexer = new Lexer(source, symbols);
        lexer.next();
        assertEquals("Lexer should hand up EOF even if there's a final EOL.", Symbol.EOF, lexer.next().getSymbol());
    }

    @Test public void lackWS() {
        Symbol keyword = new Symbol("if", Pattern.compile("if"));

        List<Symbol> symbols = singletonList(keyword);

        Reader source = new StringReader("ifif");
        Lexer lexer = new Lexer(source, symbols);
        lexer.next();
        assertEquals("Lack of whitespace should not matter.", keyword, lexer.next().getSymbol());
    }

    @Test public void lexerError() {
        Symbol keyword = new Symbol("if", Pattern.compile("if"));

        List<Symbol> symbols = singletonList(keyword);

        Reader source = new StringReader("foo");
        Lexer lexer = new Lexer(source, symbols);
        assertEquals("Lexer should signal LEXER_ERROR on no match.", Symbol.LEXER_ERROR, lexer.next().getSymbol());
    }

    @Test public void lexerErrorRecovery() {
        Symbol keyword = new Symbol("if", Pattern.compile("if"));

        List<Symbol> symbols = singletonList(keyword);

        Reader source = new StringReader("foo bar baz\nif");
        Lexer lexer = new Lexer(source, symbols);
        lexer.next();
        assertEquals("Lexer should recover from lexer error on the next line.", Symbol.EOL, lexer.next().getSymbol());
        assertEquals("Lexer should work normally after recovery.", keyword, lexer.next().getSymbol());
    }

    @Test public void insignificantSymbols() {
        Symbol keyword = new Symbol("if", Pattern.compile("if"));
        Symbol whitespace = new Symbol("ws", Pattern.compile("\\s+"), 0, false);

        List<Symbol> symbols = asList(keyword, whitespace);

        Reader source = new StringReader("if if");
        Lexer lexer = new Lexer(source, symbols);
        lexer.next();
        assertEquals("Non-significant symbols should be parsed, but not handed up.", keyword, lexer.next().getSymbol());
    }

}
