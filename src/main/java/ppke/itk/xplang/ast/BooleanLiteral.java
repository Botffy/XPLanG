package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.Scalar;
import ppke.itk.xplang.type.Type;

public class BooleanLiteral extends Literal<Boolean> {
    public BooleanLiteral(Location location, boolean value) {
        super(location, value);
    }

    @Override public Type getType() {
        return Scalar.BOOLEAN_TYPE;
    }

    @Override public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
