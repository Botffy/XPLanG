package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.Archetype;
import ppke.itk.xplang.type.Type;

public class StringLiteral extends Literal<String> {
    public StringLiteral(Location location, String value) {
        super(location, value);
    }

    @Override public Type getType() {
        return Archetype.STRING_TYPE;
    }

    @Override public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
