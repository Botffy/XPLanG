package ppke.itk.xplang.parser;

public enum SpecialName implements Name {
    /** If a typename is encountered in an Expression, then the function call will be resolved for this special name.
     * <p>
     * Register your conversion functions by this name.
     */
    TYPE_CONVERSION,

    /**
     * An unary type conversion function registered by this name can be used by the {@link TypeChecker} to coerce types
     * during function resolutions.
     */
    IMPLICIT_COERCION;

    @Override
    public String toString() {
        return "#Special$" + name();
    }
}
