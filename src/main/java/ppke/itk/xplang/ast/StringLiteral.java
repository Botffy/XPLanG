package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.Type;

public class StringLiteral extends Literal<String> {
    public StringLiteral(Location location, Type type, String value) {
        super(location, type, value);
    }

    @Override public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
