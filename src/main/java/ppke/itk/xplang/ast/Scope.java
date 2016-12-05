package ppke.itk.xplang.ast;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Lexical scope.
 *
 * A scope node is a collection of declarations.
 */
public final class Scope extends Node {
    public Scope(List<VariableDeclaration> variables) {
        this.children.addAll(0, variables);
    }

    public List<VariableDeclaration> variables() {
        return this.children.stream()
            .filter(x -> x instanceof VariableDeclaration)
            .map(x -> (VariableDeclaration) x)
            .collect(toList());
    }

    @Override public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
