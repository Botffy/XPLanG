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

    /** A type that accepts FixArrays no matter what their element type is. */
    public final static Type ANY_ARRAY = new Type("AnyArray") {
        @Override public boolean accepts(Type that) {
            return that instanceof FixArray;
        }
    };

    public final static Type ADDRESSABLE = new Type("Addressable") {
        @Override
        public boolean accepts(Type that) {
            return !Archetype.NONE.equals(that.indexType());
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
    public static final Scalar STRING_TYPE = new AddressableScalar("StringType", CHARACTER_TYPE, INTEGER_TYPE);

    public static final Scalar INSTREAM_TYPE = new Scalar("InputStreamType", Type.Initialization.INPUT_STREAM);

    private Archetype() { }
}
