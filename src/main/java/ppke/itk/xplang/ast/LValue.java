package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.Type;

/**
 * A "left value": named so because it denotes a value that may stand on the right hand side of an assignment. Refers to
 * a memory address, as opposed to the content, value found at that address. The value would be the {@link RValue}.
 */
public abstract class LValue extends Node {
    public LValue(Location location) {
        super(location);
    }

    abstract public Type getType();

    abstract public RValue toRValue();
}
