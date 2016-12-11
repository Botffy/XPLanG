package ppke.itk.xplang.interpreter;

/**
 * A value referencing another Value, probably one contained in something {@link Addressable}.
 */
abstract class ReferenceValue extends Value {
    abstract void assign(Value value);
}
