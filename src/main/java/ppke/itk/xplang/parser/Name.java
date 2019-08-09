package ppke.itk.xplang.parser;

/**
 * A name by which an entity (a variable or a type) can be registered in {@link Context}.
 */
public interface Name {
    boolean equals(Object that);
    int hashCode();
    String toString();
}
