package ppke.itk.xplang.ast;

import ppke.itk.xplang.type.Scalar;
import ppke.itk.xplang.type.Type;

public class RealLiteral extends RValue {
    private final double value;

    public RealLiteral(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override public Type getType() {
        return Scalar.REAL_TYPE;
    }

    @Override public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override public String toString() {
        return String.format("REALLITERAL[%s]", Double.toString(value));
    }
}
