package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.Archetype;
import ppke.itk.xplang.type.Type;

public class IntegerLiteral extends Literal<Integer> {
    public IntegerLiteral(Location location, int value) {
        super(location, value);
    }

    @Override public Type getType() {
        return Archetype.INTEGER_TYPE;
    }

    @Override public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
