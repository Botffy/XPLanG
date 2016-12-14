package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;

/**
 * A block statement: contains a series of declarations ({@link Scope}) and a list of commands (a {@link Sequence}) of
 * {@link Statement}s.
 */
public class Block extends Statement {
    public Block(Scope scope, Sequence sequence) {
        super(Location.definedBy(scope, sequence));
        children.add(0, scope);
        children.add(1, sequence);
    }

    public Scope scope() {
        return (Scope) children.get(0);
    }

    public Sequence sequence() {
        return (Sequence) children.get(1);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
