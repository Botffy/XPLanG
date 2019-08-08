package ppke.itk.xplang.interpreter;

/**
 * A value the {@link Interpreter} works on.
 */
interface Value {
    Value copy();

    @Override
    String toString();

    @Override
    int hashCode();

    @Override
    boolean equals(Object object);
}
