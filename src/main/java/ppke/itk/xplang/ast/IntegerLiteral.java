package ppke.itk.xplang.ast;

public class IntegerLiteral extends RValue {
    @Override public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
