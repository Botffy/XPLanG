package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;

/**
 * A conditional statement.
 */
public final class Conditional extends Statement {
    public Conditional(Location location, RValue condition, Sequence trueBranch, Sequence falseBranch) {
        super(location);
        this.children.add(0, condition);
        this.children.add(1, trueBranch);
        this.children.add(2, falseBranch);
    }

    public RValue getCondition() {
        return (RValue) children.get(0);
    }

    public Sequence getTrueSequence() {
        return (Sequence) children.get(1);
    }

    public Sequence getElseSequence() {
        return (Sequence) children.get(2);
    }

    @Override public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
