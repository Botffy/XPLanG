package ppke.itk.xplang.parser;

public class NameError extends SemanticError {
    private final static String NAME_ERROR_MESSAGE = "'%s' does not exist.";

    NameError(Token token) {
        this(NAME_ERROR_MESSAGE, token);
    }

    NameError(String message, Token token) {
        super(String.format(message, token.lexeme()), token.location());
    }
}
