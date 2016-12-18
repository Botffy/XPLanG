package ppke.itk.xplang.parser;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.common.Translator;

public class FunctionResolutionError extends SemanticError {
    private final static Translator translator = Translator.getInstance("parser");

    public FunctionResolutionError(Location location) {
        this(translator.translate("parser.FunctionResolutionError.message"), location);
    }

    public FunctionResolutionError(String message, Location location) {
        super(message, location);
    }
}
