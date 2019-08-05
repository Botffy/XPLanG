package ppke.itk.xplang.parser;


import ppke.itk.xplang.common.Translator;

import java.util.Collection;
import java.util.Collections;

/**
 * Thrown when the source code does not conform the expected structure of the language.
 */
public class SyntaxError extends ParseError {
    private final static Translator translator = Translator.getInstance("parser");

    /**
     * Signal a syntax error with the given message.
     * @param message A custom message to show the programmer. May include two conversion characters, the first
     *                converting {@code expected}, the other converting {@code actual}
     * @param expected We expected any of these symbols.
     * @param actual But we found this one.
     * @param token The token that caused the error.
     */
    public SyntaxError(String message, Collection<Symbol> expected, Symbol actual, Token token) {
        super(
            String.format(message, expected, actual),
            token.location()
        );
    }

    /**
     * Signal a syntax error with the default, hardcoded message.
     * @param expected A collection of symbols. Should not be empty, may contain only a single element.
     * @param actual The unexpected symbol.
     * @param token The token causing the error.
     */
    public SyntaxError(Collection<Symbol> expected, Symbol actual, Token token) {
        this(translator.translate(expected.size() > 1?
                "parser.SyntaxError.message.expectMany" : "parser.SyntaxError.message.expectOne"
            ), expected, actual, token);
    }

    /**
     * Signal a syntax error with the default, hardcoded message. Convenience constructor
     * @param expected The symbol we expected.
     * @param actual The unexpected symbol.
     * @param token The token causing the error.
     */
    public SyntaxError(Symbol expected, Symbol actual, Token token) {
        this(translator.translate("parser.SyntaxError.message.expectOne"), expected, actual, token);
    }

    /**
     * Signal a syntax error with a custom message.
     * @param message A custom message to show the programmer. May include two conversion characters, the first
     *                converting {@code expected}, the other converting {@code actual}
     * @param expected The symbol we expected.
     * @param actual The unexpected symbol.
     * @param token The token causing the error.
     */
    public SyntaxError(String message, Symbol expected, Symbol actual, Token token) {
        this(message, Collections.singletonList(expected), actual, token);
    }

    public SyntaxError(String message, Token token) {
        super(message, token.location());
    }

}

