package ppke.itk.xplang.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A node in the abstract syntax tree.
 */
public abstract class Node {
    protected List<Node> children = new ArrayList<>();

    /**
     * Get the children of this Node.
     * @return an unmodifiable list of the children of this node.
     */
    public List<Node> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /**
     * Gets the {@code index}th child of this node.
     * @param index the ordinal of the child.
     * @return the {@code index}th child if it exists.
     * @throws ArrayIndexOutOfBoundsException if the child does not exist.
     */
    public Node getChild(int index) throws ArrayIndexOutOfBoundsException {
        return children.get(index);
    }

    /**
     * A string representation, to be used internally, in debug messages, etc.
     *
     * Subclasses may override this to give additional details about themselves in square brackets. These additional
     * details must not be about their children, only themselves.
     *
     * @return a string the label describing the AST node.
     */
    @Override public String toString() {
        return this.getClass().getSimpleName().toUpperCase();
    }

    /**
     * Accept the visitor.
     * @param visitor The algorithm applied to this node.
     */
    abstract public void accept(ASTVisitor visitor);
}
