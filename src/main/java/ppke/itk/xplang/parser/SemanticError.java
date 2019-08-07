package ppke.itk.xplang.parser;

import ppke.itk.xplang.common.Location;

public abstract class SemanticError extends ParseError {
    protected SemanticError(String message, Location location) {
        super(message, location);
    }
}
