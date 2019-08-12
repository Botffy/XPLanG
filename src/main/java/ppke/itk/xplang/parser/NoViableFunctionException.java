package ppke.itk.xplang.parser;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.common.Translator;
import ppke.itk.xplang.type.Signature;
import ppke.itk.xplang.type.Type;

import java.util.List;
import java.util.Set;

public class NoViableFunctionException extends FunctionResolutionError {
    private final static Translator translator = Translator.getInstance("parser");

    NoViableFunctionException(Name name, Set<Signature> candidates, List<Type> actualTypes, Location location) {
        super(
            translator.translate("parser.typechecker.noViableFunction.message", name, candidates, actualTypes),
            location
        );
    }
}
