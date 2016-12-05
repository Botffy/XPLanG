package ppke.itk.xplang.parser;

import ppke.itk.xplang.common.Location;

public class SemanticError extends ParseError {
    SemanticError(String message, Location location) {
        super(message, location);
    }
}
