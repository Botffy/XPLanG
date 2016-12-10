package ppke.itk.xplang.ast;

public class Conditional extends Statement {
    public Conditional() {
    }

    @Override public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
