package ppke.itk.xplang.parser;

import ppke.itk.xplang.common.Translator;

public class NoSuchVariableException extends NameError {
    private final static Translator translator = Translator.getInstance("parser");

    public NoSuchVariableException(Name name, Token token) {
        super(
            translator.translate("parser.NameError.NoSuchVariableException.message", name),
            token
        );
    }
}
