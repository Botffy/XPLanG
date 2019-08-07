package ppke.itk.xplang.parser;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.common.Translator;
import ppke.itk.xplang.type.Type;

public class TypeError extends SemanticError {
    private final static Translator translator = Translator.getInstance("parser");

    public TypeError(String message, Location location) {
        super(message, location);
    }

    public TypeError(Type expected, Type actual, Location location) {
        super(
            translator.translate("parser.typechecker.typeError.message", expected, actual),
            location
        );
    }
}
