package ppke.itk.xplang.ast;

import ppke.itk.xplang.type.Scalar;
import ppke.itk.xplang.type.Type;

public class StringLiteral extends RValue {
    private final String value;

    public StringLiteral(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override public Type getType() {
        return Scalar.STRING_TYPE;
    }

    @Override public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override public String toString() {
        return String.format("STRINGLITERAL[%s]", value);
    }
}
