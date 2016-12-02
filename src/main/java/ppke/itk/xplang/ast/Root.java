package ppke.itk.xplang.ast;

import ppke.itk.xplang.parser.Symbol;

/**
 * The root node of the AST. Essentially the global scope.
 */
public class Root extends Scope {
    @Override public void accept(ASTVisitor visitor) { visitor.visit(this); }

    public Root(Program entryPoint) {
        children.add(0, entryPoint);
    }

    /**
     * The entry point of the application.
     * @return the program to be executed when this AST is processed.
     */
    public Program entryPoint() {
        return (Program) children.get(0);
    }

    @Override public String toString() {
        return "Root scope";
    }
}