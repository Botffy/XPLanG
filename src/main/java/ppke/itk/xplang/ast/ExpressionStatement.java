package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;

public class ExpressionStatement extends Statement {
    public ExpressionStatement(Location location, RValue rValue) {
        super(location);
        this.children.add(0, rValue);
    }

    public RValue getExpression() {
        return (RValue) this.children.get(0);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
