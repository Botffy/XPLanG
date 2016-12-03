package ppke.itk.xplang.ast;

/**
 * A block statement: contains a series of declarations ({@link Scope}) and a list of commands (a {@link Sequence}) of
 * {@link Statement}s.
 */
public class Block extends Statement {
    public Block(Scope scope, Sequence sequence) {
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

    @Override
    public String toString() {
        return "Block";
    }
}
