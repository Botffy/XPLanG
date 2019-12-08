package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.Archetype;
import ppke.itk.xplang.type.Signature;

import java.util.List;

public abstract class FunctionDeclaration extends Node {
    private final Signature signature;

    protected FunctionDeclaration(Location location, Signature signature) {
        super(location);
        this.signature = signature;
    }

    public Signature signature() {
        return signature;
    }

    public FunctionCall call(Location location, List<RValue> args) {
        return new FunctionCall(location, this, args);
    }

    final public boolean isProcedure() {
        return this.signature.getReturnType().equals(Archetype.NONE);
    }

    abstract public boolean isDefined();

    /**
     * A function is considered "pure" if it does not interact with its outer scope at all.
     */
    abstract public boolean isPure();

    @Override public String toString() {
        return String.format("FUNCTION[%s]", signature);
    }
}
