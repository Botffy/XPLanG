package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.Type;

public class CharacterLiteral extends Literal<Character> {
    public CharacterLiteral(Location location, Type type, char value) {
        super(location, type, value);
    }

    @Override public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
