package ppke.itk.xplang.parser;

public enum SpecialName implements Name {
    /** If a typename is encountered in an Expression, then the function call will be resolved for this special name.
     * <p>
     * Register your conversion functions by this name.
     */
    TYPE_CONVERSION;

    @Override
    public String toString() {
        return "#Special$" + name();
    }
}
