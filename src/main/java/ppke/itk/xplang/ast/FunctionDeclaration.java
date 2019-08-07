package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.Signature;

public abstract class FunctionDeclaration extends Node {
    private final Signature signature;

    protected FunctionDeclaration(Location location, Signature signature) {
        super(location);
        this.signature = signature;
    }

    public Signature signature() {
        return signature;
    }

    @Override public String toString() {
        return String.format("FUNCTION[%s]", signature);
    }
}
