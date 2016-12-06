package ppke.itk.xplang.ast;

public class IntegerLiteral extends RValue {
    private final int value;

    public IntegerLiteral(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override public String toString() {
        return String.format("INTEGERLITERAL[%s]", value);
    }
}
