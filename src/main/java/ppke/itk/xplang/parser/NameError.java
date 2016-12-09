package ppke.itk.xplang.parser;

import ppke.itk.xplang.common.Translator;

public class NameError extends SemanticError {
    private final static Translator translator = Translator.getInstance("parser");

    NameError(Token token) {
        this(translator.translate("parser.NameError.message"), token);
    }

    NameError(String message, Token token) {
        super(String.format(message, token.lexeme()), token.location());
    }
}
