package ppke.itk.xplang.ast;

/**
 * The value of some variable.
 *
 * This tells the interpreter to load the value of the referenced variable.
 */
public class VarVal extends RValue {
    /**
     * Constructor.
     * @param var The variable this reference refers to.
     */
    public VarVal(VariableDeclaration var) {
        this.children.add(0, var);
    }

    public VariableDeclaration getVariable() {
        return (VariableDeclaration) children.get(0);
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
