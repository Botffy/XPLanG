package ppke.itk.xplang.ast;

/**
 * Reference to a variable. Its memory address.
 *
 * This tells the interpreter to load the address of this variable, we're going to do something with that (probably
 * we're just going to overwrite it).
 */
public final class VarRef extends LValue {
    /**
     * Constructor.
     * @param var The variable this reference refers to.
     */
    public VarRef(VariableDeclaration var) {
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
        return String.format("VARREF[%s]", getName());
    }
}
