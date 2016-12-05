package ppke.itk.xplang.parser;

/**
 * An error of the {@link Lexer}. Thrown when the Lexer encounters a piece of text it cannot match to any of the
 * {@link Symbol}s it knows.
 */
public class LexerError extends ParseError {
    /**
     * Signal a lexing error.
     * @param token Lexer is supposed to return the rest of the line, starting from the point of error.
     */
    LexerError(Token token) {
        super(String.format("Could not tokenize '%s'", token.lexeme()), token.location());
    }
}
