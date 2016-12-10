package ppke.itk.xplang.ast;

/**
 * A conditional statement.
 */
public final class Conditional extends Statement {
    public Conditional(RValue condition, Sequence sequence) {
        this.children.add(0, condition);
        this.children.add(1, sequence);
    }

    public RValue getCondition() {
        return (RValue) children.get(0);
    }

    public Sequence getSequence() {
        return (Sequence) children.get(1);
    }

    @Override public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
