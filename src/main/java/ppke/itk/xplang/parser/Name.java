package ppke.itk.xplang.parser;

/**
 * A name by which an entity (a variable or a type) can be registered in {@link Context}.
 */
public abstract class Name {
    @Override abstract public boolean equals(Object that);
    @Override abstract public int hashCode();
    @Override abstract public String toString();
}
