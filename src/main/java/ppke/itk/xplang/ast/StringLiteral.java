package ppke.itk.xplang.ast;

import ppke.itk.xplang.type.Scalar;
import ppke.itk.xplang.type.Type;

public class StringLiteral extends Literal<String> {
    public StringLiteral(String value) {
        super(value);
    }

    @Override public Type getType() {
        return Scalar.STRING_TYPE;
    }

    @Override public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
