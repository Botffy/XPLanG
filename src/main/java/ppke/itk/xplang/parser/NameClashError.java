package ppke.itk.xplang.parser;

import ppke.itk.xplang.common.Translator;

/**
 * Thrown when you try to declare something by a name, but that name is already taken in that scope.
 */
public class NameClashError extends SemanticError {
    private final static Translator translator = Translator.getInstance("parser");

    NameClashError(Token token) {
        this(translator.translate("parser.NameClashError.message"), token);
    }

    NameClashError(String message, Token token) {
        super(String.format(message, token.lexeme()), token.location());
    }
}
