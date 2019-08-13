package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.Type;

public class BooleanLiteral extends Literal<Boolean> {
    public BooleanLiteral(Location location, Type type, boolean value) {
        super(location, type, value);
    }

    @Override public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
