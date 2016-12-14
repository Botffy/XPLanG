package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;

/**
 * The root node of the AST. Essentially the global scope.
 */
// FIXME should include a Scope
public class Root extends Node {
    @Override public void accept(ASTVisitor visitor) { visitor.visit(this); }

    public Root(Location location, Program entryPoint) {
        super(location);
        children.add(0, entryPoint);
    }

    /**
     * The entry point of the application.
     * @return the program to be executed when this AST is processed.
     */
    public Program entryPoint() {
        return (Program) children.get(0);
    }
}
