package ppke.itk.xplang.parser;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.common.Translator;
import ppke.itk.xplang.type.Signature;
import ppke.itk.xplang.type.Type;

import java.util.List;
import java.util.Set;

class FunctionAmbiguousException extends FunctionResolutionError {
    private final static Translator translator = Translator.getInstance("parser");

    FunctionAmbiguousException(Name name, Set<Signature> valid, List<Type> actualTypes, Location location) {
        super(
            translator.translate("parser.typechecker.functionAmbiguous.message", name, valid, actualTypes),
            location
        );
    }
}
