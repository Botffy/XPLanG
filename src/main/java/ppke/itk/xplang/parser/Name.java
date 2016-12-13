package ppke.itk.xplang.parser;

/**
 * A name by which an entity (a variable or a type) can be registered in {@link Context}.
 *
 * Something of a marker interface, implementors should make sure to implement both equals and hashCode.
 */
public interface Name {
    @Override boolean equals(Object that);
    @Override int hashCode();
}
