package ppke.itk.xplang.parser;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.SemanticError;

abstract class FunctionResolutionError extends SemanticError {
    protected FunctionResolutionError(String message, Location location) {
        super(message, location);
    }
}
