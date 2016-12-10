package ppke.itk.xplang.ast;

import ppke.itk.xplang.type.Type;

/**
 * The value of some variable.
 *
 * This tells the interpreter to load the value of the referenced variable.
 */
public class VarVal extends RValue {
    private final VariableDeclaration var;

    /**
     * Constructor.
     * @param var The variable this reference refers to.
     */
    public VarVal(VariableDeclaration var) {
        this.var = var;
    }

    public VariableDeclaration getVariable() {
        return this.var;
    }

    public String getName() {
        return getVariable().getName();
    }

    @Override public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override public String toString() {
        return String.format("VARVAL[%s]", getName());
    }
}
