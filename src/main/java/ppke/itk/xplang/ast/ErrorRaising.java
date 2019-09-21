package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;

public class ErrorRaising extends Statement {
    public ErrorRaising(Location location, RValue errorMessage) {
        super(location);
        this.children.add(0, errorMessage);
    }

    public RValue getErrorMessage() {
        return (RValue) this.children.get(0);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
