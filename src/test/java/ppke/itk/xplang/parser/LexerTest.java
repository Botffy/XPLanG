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
            variable, lexer.next().symbol()
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
            keyword, lexer.next().symbol()
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
            keyword, lexer.next().symbol()
        );
    }

    @Test public void eof() {
        Symbol keyword = new Symbol("if", Pattern.compile("if"));

        List<Symbol> symbols = singletonList(keyword);

        Reader source = new StringReader("if");
        Lexer lexer = new Lexer(source, symbols);
        lexer.next();
        assertEquals("Lexer should hand up EOF at the end of the source.", Symbol.EOF, lexer.next().symbol());
    }

    @Test public void eol() {
        Symbol keyword = new Symbol("if", Pattern.compile("if"));
        Symbol eol = new Symbol("EOL", Pattern.compile("[\r\n]+"));

        List<Symbol> symbols = asList(keyword, eol);

        Reader source = new StringReader("if\nif");
        Lexer lexer = new Lexer(source, symbols);
        lexer.next();
        assertEquals("Lexer should hand up EOL at line end.", eol, lexer.next().symbol());
    }

    @Test public void lackWS() {
        Symbol keyword = new Symbol("if", Pattern.compile("if"));

        List<Symbol> symbols = singletonList(keyword);

        Reader source = new StringReader("ifif");
        Lexer lexer = new Lexer(source, symbols);
        lexer.next();
        assertEquals("Lack of whitespace should not matter.", keyword, lexer.next().symbol());
    }

    @Test public void lexerError() {
        Symbol keyword = new Symbol("if", Pattern.compile("if"));

        List<Symbol> symbols = singletonList(keyword);

        Reader source = new StringReader("foo\nif");
        Lexer lexer = new Lexer(source, symbols);
        Token tok = lexer.next();
        assertEquals("Lexer should signal LEXER_ERROR on no match.", Symbol.LEXER_ERROR, tok.symbol());
        assertEquals("Lexer should put the rest of the line in the token handed up.",
            "foo", tok.lexeme()
        );
    }

    @Test public void lexerErrorRecovery() {
        Symbol keyword = new Symbol("if", Pattern.compile("if"));

        List<Symbol> symbols = singletonList(keyword);
        Reader source = new StringReader("foo bar baz\nifif");
        Lexer lexer = new Lexer(source, symbols);
        lexer.next();
        assertEquals("Lexer should recover from lexer error on the next line.",
            "if", lexer.next().lexeme()
        );
        assertEquals("Lexer should work normally after recovery.", "if", lexer.next().lexeme());
        assertEquals("Lexer should work normally after recovery.", Symbol.EOF, lexer.next().symbol());
    }

    @Test public void insignificantSymbols() {
        Symbol keyword = new Symbol("if", Pattern.compile("if"));
        Symbol whitespace = new Symbol("ws", Pattern.compile("\\s+"), 0, false);

        List<Symbol> symbols = asList(keyword, whitespace);

        Reader source = new StringReader("if if");
        Lexer lexer = new Lexer(source, symbols);
        lexer.next();
        assertEquals("Non-significant symbols should be parsed, but not handed up.", keyword, lexer.next().symbol());
    }

    @Test public void shouldReportLineNoCorrectly() {
        Symbol keyword = new Symbol("if", Pattern.compile("if"));
        Symbol whitespace = new Symbol("ws", Pattern.compile("\\s+"), Symbol.Precedence.DEFAULT, false);
        List<Symbol> symbols = asList(keyword, whitespace);

        Reader source = new StringReader("if\nif\n\nifif");
        Lexer lexer = new Lexer(source, symbols);
        assertEquals("The first if should be on the first line", 1, lexer.next().getLine());
        assertEquals("The second if should be on the second line", 2, lexer.next().getLine());
        assertEquals("The third if should be on the fourth line", 4, lexer.next().getLine());
        assertEquals("The fourth if should be on the fourth line", 4, lexer.next().getLine());
    }
}
