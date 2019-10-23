package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;

import java.util.Collection;

import static java.util.stream.Collectors.toList;

/**
 * Lexical scope.
 *
 * A scope node is a collection of declarations.
 */
public final class Scope extends Node {
    public Scope(Collection<VariableDeclaration> variables) {
        super(Location.definedBy(variables));
        this.children.addAll(0, variables);
    }

    public Collection<VariableDeclaration> variables() {
        return this.children.stream()
            .filter(x -> x instanceof VariableDeclaration)
            .map(x -> (VariableDeclaration) x)
            .collect(toList());
    }

    @Override public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
