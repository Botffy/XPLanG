package ppke.itk.xplang.ast;

public class Incrementation extends Statement {
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Increment";
    }
}
