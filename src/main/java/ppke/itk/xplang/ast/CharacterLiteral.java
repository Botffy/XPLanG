package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.Archetype;
import ppke.itk.xplang.type.Type;

public class CharacterLiteral extends Literal<Character> {
    public CharacterLiteral(Location location, char value) {
        super(location, value);
    }

    @Override public Type getType() {
        return Archetype.CHARACTER_TYPE;
    }

    @Override public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
