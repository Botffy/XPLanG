package ppke.itk.xplang.type;

public class Scalar extends Type {
    /** Integer archetype. */
    public static final Scalar INTEGER_TYPE = new Scalar("IntegerType");

    /** Boolean archetype. */
    public static final Scalar BOOLEAN_TYPE = new Scalar("BooleanType");

    /** Character archetype. */
    public static final Scalar CHARACTER_TYPE = new Scalar("CharacterType");

    /** Real archetype. */
    public static final Scalar REAL_TYPE = new Scalar("RealType");

    /** String archetype. */
    public static final Scalar STRING_TYPE = new Scalar("StringType") {
        @Override public Type elementType() {
            return CHARACTER_TYPE;
        }

        @Override public Type indexType() {
            return INTEGER_TYPE;
        }
    };

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

    // FIXME we only use this to determine if the value should be initialized?
    @Override public boolean isScalar() {
        return true;
    }
}
