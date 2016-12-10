package ppke.itk.xplang.parser;

import ppke.itk.xplang.common.Location;

public class TypeError extends SemanticError {
    public TypeError(String message, Location location) {
        super(message, location);
    }
}
