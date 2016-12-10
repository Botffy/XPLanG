package ppke.itk.xplang.ast;

import ppke.itk.xplang.type.Scalar;
import ppke.itk.xplang.type.Type;

public class IntegerLiteral extends RValue {
    private final int value;

    public IntegerLiteral(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override public Type getType() {
        return Scalar.INTEGER_TYPE;
    }

    @Override public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override public String toString() {
        return String.format("INTEGERLITERAL[%s]", value);
    }
}
