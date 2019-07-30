package ppke.itk.xplang.type;

/**
 *  A fixed length array.
 */
public final class FixArray extends Type {
    public final static Type ANY_ARRAY = new Type("ANY_ARRAY") {
        @Override public boolean accepts(Type that) {
            return that instanceof FixArray;
        }
    };

    private final Type elemType;
    private final int length;

    public static FixArray of(int length, Type elemType) {
        return new FixArray(length, elemType);
    }

    private FixArray(int length, Type elemType) {
        super("Array");
        this.elemType = elemType;
        this.length = length;
    }

    @Override public Type elementType() {
        return elemType;
    }

    @Override public Type indexType() {
        return Archetype.INTEGER_TYPE;
    }

    /**
     * A limited structural equivalence: a FixArray accepts other FixArrays with the same length, whose elementType
     * our elementType accepts.
     */
    @Override public boolean accepts(Type other) {
        if(!(other instanceof FixArray)) return false;
        FixArray that = (FixArray) other;
        return this.length == that.length && this.elemType.accepts(that.elemType);
    }

    @Override public int size() {
        return this.length;
    }

    @Override public boolean isScalar() {
        return false;
    }

    @Override public String toString() {
        return String.format("[Array of %d %s]", length, elemType);
    }
}
