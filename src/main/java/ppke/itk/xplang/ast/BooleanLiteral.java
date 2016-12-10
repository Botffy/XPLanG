package ppke.itk.xplang.ast;

import ppke.itk.xplang.type.Scalar;
import ppke.itk.xplang.type.Type;

public class BooleanLiteral extends RValue {
    private final boolean value;

    public BooleanLiteral(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override public Type getType() {
        return Scalar.BOOLEAN_TYPE;
    }

    @Override public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override public String toString() {
        return String.format("BOOLEANLITERAL[%s]", value);
    }
}
