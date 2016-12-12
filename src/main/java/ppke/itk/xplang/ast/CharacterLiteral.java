package ppke.itk.xplang.ast;

import ppke.itk.xplang.type.Scalar;
import ppke.itk.xplang.type.Type;

public class CharacterLiteral extends RValue {
    private final char value;

    public CharacterLiteral(char value) {
        this.value = value;
    }

    public char getValue() {
        return value;
    }

    @Override public Type getType() {
        return Scalar.CHARACTER_TYPE;
    }

    @Override public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override public String toString() {
        return String.format("CHARACTERLITERAL[%s]", value);
    }
}
