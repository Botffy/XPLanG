package ppke.itk.xplang.parser;

public enum SpecialName implements Name {
    /**
     * A procedure returns a null value under the hood.
     */
    NULL_RESULT,

    /** If a typename is encountered in an Expression, then the function call will be resolved for this special name.
     * <p>
     * Register your conversion functions by this name.
     */
    TYPE_CONVERSION,

    /**
     * An unary type conversion function registered by this name can be used by the {@link TypeChecker} to coerce types
     * during function resolutions.
     */
    IMPLICIT_COERCION,

    /**
     * A nullary operator reading from the standard input.
     */
    READ_INPUT,

    /** Open a file for reading. */
    OPEN_INPUT_FILE,

    /** Open a file for writing. */
    OPEN_OUTPUT_FILE,

    /** Close an open inputstream. */
    CLOSE_INPUTSTREAM,

    /** Close an open outputstream. */
    CLOSE_OUTPUTSTREAM;

    @Override
    public String toString() {
        return "#Special$" + name();
    }
}
