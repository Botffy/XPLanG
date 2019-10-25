package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.Type;

/**
 * The value of some variable.
 *
 * This tells the interpreter to load the value of the referenced variable.
 */
public final class VarVal extends RValue {
    private final VariableDeclaration var;
    private final boolean isConstant;

    /**
     * Constructor.
     * @param var The variable this reference refers to.
     */
    public VarVal(Location location, VariableDeclaration var, boolean isConstant) {
        super(location);
        this.var = var;
        this.isConstant = isConstant;
    }

    public VariableDeclaration getVariable() {
        return this.var;
    }

    public String getName() {
        return this.var.getName();
    }

    @Override public Type getType() {
        return var.getType();
    }

    @Override
    public boolean isStatic() {
        return isConstant;
    }

    @Override public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override public String toString() {
        return String.format("VARVAL[%s]", getName());
    }
}
