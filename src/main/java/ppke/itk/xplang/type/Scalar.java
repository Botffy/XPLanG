package ppke.itk.xplang.type;

public class Scalar extends Type {
    /** Integer archetype. */
    public static final Scalar INTEGER_TYPE = new Scalar("IntegerType");

    /** Boolean archetype. */
    public static final Scalar BOOLEAN_TYPE = new Scalar("BooleanType");

    public Scalar(String label) {
        super(label);
    }

    /**
     * A Scalar is equal to another if they are represented by the same object. This is something of a nominative
     * equivalence, though a Grammar may register the same type under different aliases.
     */
    @Override public boolean accepts(Type that) {
        return this == that;
    }

    @Override public int size() {
        return 0;
    }

    @Override public boolean isScalar() {
        return true;
    }
}
