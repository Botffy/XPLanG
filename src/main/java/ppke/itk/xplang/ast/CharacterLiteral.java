package ppke.itk.xplang.ast;

import ppke.itk.xplang.type.Scalar;
import ppke.itk.xplang.type.Type;

public class CharacterLiteral extends Literal<Character> {
    public CharacterLiteral(char value) {
        super(value);
    }

    @Override public Type getType() {
        return Scalar.CHARACTER_TYPE;
    }

    @Override public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
