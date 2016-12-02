package ppke.itk.xplang.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A node in the abstract syntax tree.
 */
public abstract class Node {
    /**
     * Accept the visitor.
     * @param visitor The algorithm applied to this node.
     */
    abstract public void accept(ASTVisitor visitor);

    protected List<Node> children = new ArrayList<>();

    /**
     * Get the children of this Node.
     * @return an unmodifiable list of the children of this node.
     */
    public List<Node> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /**
     * Gets the {@code index}th child of this node
     * @param index the ordinal of the child.
     * @return the {@code index}th child if it exists.
     * @throws ArrayIndexOutOfBoundsException if the child does not exist.
     */
    public Node getChild(int index) throws ArrayIndexOutOfBoundsException {
        return children.get(index);
    }

    @Override abstract public String toString();
}