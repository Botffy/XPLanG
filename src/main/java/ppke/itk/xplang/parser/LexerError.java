package ppke.itk.xplang.parser;

import ppke.itk.xplang.common.Translator;

/**
 * An error of the {@link Lexer}. Thrown when the Lexer encounters a piece of text it cannot match to any of the
 * {@link Symbol}s it knows.
 */
public class LexerError extends ParseError {
    private final static Translator translator = Translator.getInstance("parser");

    /**
     * Signal a lexing error.
     * @param token Lexer is supposed to return the rest of the line, starting from the point of error.
     */
    LexerError(Token token) {
        super(translator.translate("parser.LexerError.message", token.lexeme()), token.location());
    }
}
