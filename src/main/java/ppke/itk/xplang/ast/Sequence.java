package ppke.itk.xplang.ast;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * An ordered list of {@link Statement}s.
 */
public class Sequence extends Node {
    @Override public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    public Sequence(List<Statement> statements) {
        this.children.addAll(statements);
    }

    public List<Statement> statements() {
        return children.stream()
            .map(x -> (Statement) x)
            .collect(toList());
    }

    @Override public String toString() {
        return null;
    }
}
