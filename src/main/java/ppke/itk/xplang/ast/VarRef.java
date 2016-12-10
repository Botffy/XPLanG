package ppke.itk.xplang.ast;

import ppke.itk.xplang.type.Type;

/**
 * Reference to a variable. Its memory address.
 *
 * This tells the interpreter to load the address of this variable, we're going to do something with that (probably
 * we're just going to overwrite it).
 */
public final class VarRef extends LValue {
    private final VariableDeclaration var;

    /**
     * Constructor.
     * @param var The variable this reference refers to.
     */
    public VarRef(VariableDeclaration var) {
        this.var = var;
    }

    public VariableDeclaration getVariable() {
        return this.var;
    }

    public String getName() {
        return this.var.getName();
    }

    @Override public Type getType() {
        return this.var.getType();
    }

    @Override public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override public String toString() {
        return String.format("VARREF[%s]", getName());
    }
}
