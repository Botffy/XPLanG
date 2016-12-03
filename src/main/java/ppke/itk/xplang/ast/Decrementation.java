package ppke.itk.xplang.ast;

public class Decrementation extends Statement {
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
