package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;

/**
 * The root node of the AST. Essentially the global scope.
 */
public class Root extends Node {
    @Override public void accept(ASTVisitor visitor) { visitor.visit(this); }

    public Root(Location location, Program entryPoint, Scope scope) {
        super(location);
        children.add(0, entryPoint);
        children.add(1, scope);
    }

    /**
     * The entry point of the application.
     * @return the program to be executed when this AST is processed.
     */
    public Program entryPoint() {
        return (Program) children.get(0);
    }

    public Scope scope() {
        return (Scope) children.get(1);
    }
}
