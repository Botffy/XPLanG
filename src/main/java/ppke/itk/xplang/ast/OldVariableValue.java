package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.Type;

public class OldVariableValue extends RValue {
    private final VariableDeclaration var;

    public OldVariableValue(Location location, VariableDeclaration var) {
        super(location);
        this.var = var;
    }

    public VariableDeclaration getVariable() {
        return var;
    }

    @Override
    public Type getType() {
        return var.getType();
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
