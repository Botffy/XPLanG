package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;

public class Assertion extends Statement {
    public Assertion(Location location, RValue condition) {
        super(location);
        this.children.add(0, condition);
    }

    public RValue getCondition() {
        return (RValue) this.children.get(0);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
