package ppke.itk.xplang.ast;

/**
 * Assign a value to a memory location.
 */
public class Assignment extends Statement {
    public Assignment(LValue lhs, RValue rhs) {
        this.children.add(0, lhs);
        this.children.add(1, rhs);
    }

    public LValue getLHS() {
        return (LValue) children.get(0);
    }

    public RValue getRHS() {
        return (RValue) children.get(1);
    }

    @Override public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
