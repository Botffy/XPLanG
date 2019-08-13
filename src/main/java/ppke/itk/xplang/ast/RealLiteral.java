package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.Type;

public class RealLiteral extends Literal<Double> {
    public RealLiteral(Location location, Type type, double value) {
        super(location, type, value);
    }

    @Override public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
