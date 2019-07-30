package ppke.itk.xplang.type;

public class Archetype {
    /**
     *  The Any formal type: the type that accepts all other types.
     */
    public final static Type ANY = new Type("Any") {
        @Override public boolean accepts(Type that) {
            return true;
        }
    };

    /**
     *  The None formal type: the type that accepts no other types.
     */
    public final static Type NONE = new Type("None") {
        @Override public boolean accepts(Type that) {
            return false;
        }
    };

    /** Integer archetype. */
    public static final Scalar INTEGER_TYPE = new Scalar("IntegerType");

    /** Real archetype. */
    public static final Scalar REAL_TYPE = new Scalar("RealType");

    /** Boolean archetype. */
    public static final Scalar BOOLEAN_TYPE = new Scalar("BooleanType");

    /** Character archetype. */
    public static final Scalar CHARACTER_TYPE = new Scalar("CharacterType");

    /** String archetype. */
    public static final Scalar STRING_TYPE = new Scalar("StringType") {
        @Override public Type elementType() {
            return CHARACTER_TYPE;
        }

        @Override public Type indexType() {
            return INTEGER_TYPE;
        }
    };

    private Archetype() { }
}
