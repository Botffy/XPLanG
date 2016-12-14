package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;

/**
 * The smallest element of the language.
 */
public abstract class Statement extends Node {
    protected Statement(Location location) {
        super(location);
    }
}
