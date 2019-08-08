package ppke.itk.xplang.interpreter;

/**
 * A value referencing another ValueExpression, probably one contained in something {@link Addressable}.
 */
interface ReferenceValue extends Value {
    void assign(Value value);
}
