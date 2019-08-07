package ppke.itk.xplang.parser;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.common.Translator;

public class NoViableFunctionException extends FunctionResolutionError {
    private final static Translator translator = Translator.getInstance("parser");

    NoViableFunctionException(Name name, Location location) {
        super(
            translator.translate("parser.typechecker.noViableFunction.message", name),
            location
        );
    }
}
