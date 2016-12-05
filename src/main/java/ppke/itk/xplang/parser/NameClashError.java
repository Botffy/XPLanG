package ppke.itk.xplang.parser;

/**
 * Thrown when you try to declare something by a name, but that name is already taken in that scope.
 */
public class NameClashError extends SemanticError {
    private final static String NAME_CLASH_MESSAGE = "Could not declare '%s': name already taken.";

    NameClashError(Token token) {
        this(NAME_CLASH_MESSAGE, token);
    }

    NameClashError(String message, Token token) {
        super(String.format(message, token.lexeme()), token.location());
    }
}
