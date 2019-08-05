package ppke.itk.xplang.parser;

import ppke.itk.xplang.common.Translator;

public class NotVariableException extends NameError {
    private final static Translator translator = Translator.getInstance("parser");

    public NotVariableException(Name name, String foundEntity, Token token) {
        super(
            translator.translate("parser.NameError.NotVariableException.message", name, foundEntity),
            token
        );
    }
}
