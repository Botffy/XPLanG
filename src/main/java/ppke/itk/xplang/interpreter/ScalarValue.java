package ppke.itk.xplang.interpreter;

/**
 * A Value the underlying Java value can be queried.
 */
public interface ScalarValue<T> extends Value {
    T getValue();
}
