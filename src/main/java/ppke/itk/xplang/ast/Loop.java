package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;

/**
 * A conditional statement.
 */
public final class Loop extends Statement {
    private Type type;

    public Loop(Location location, RValue condition, Sequence sequence, Type type) {
        super(location);
        this.type = type;
        this.children.add(0, condition);
        this.children.add(1, sequence);
    }

    public Type getType() {
        return type;
    }

    public RValue getCondition() {
        return (RValue) children.get(0);
    }

    public Sequence getSequence() {
        return (Sequence) children.get(1);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    public enum Type {
        TEST_FIRST,
        TEST_LAST
    }
}
