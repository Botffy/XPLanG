package ppke.itk.xplang.ast;

public class Incrementation extends Statement {
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
