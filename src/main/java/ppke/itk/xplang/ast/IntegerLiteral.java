package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.Type;

public class IntegerLiteral extends Literal<Integer> {
    public IntegerLiteral(Location location, Type type, int value) {
        super(location, type, value);
    }

    @Override public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
