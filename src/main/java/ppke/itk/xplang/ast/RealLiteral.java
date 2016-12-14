package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.Scalar;
import ppke.itk.xplang.type.Type;

public class RealLiteral extends Literal<Double> {
    public RealLiteral(Location location, double value) {
        super(location, value);
    }

    @Override public Type getType() {
        return Scalar.REAL_TYPE;
    }

    @Override public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
