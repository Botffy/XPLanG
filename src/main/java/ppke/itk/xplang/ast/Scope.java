package ppke.itk.xplang.ast;

/**
 * Lexical scope.
 *
 * A scope node is a collection of declarations.
 */
public class Scope extends Node {
    @Override public void accept(ASTVisitor visitor) { visitor.visit(this); }

    @Override
    public String toString() {
        return "Scope";
    }
}
