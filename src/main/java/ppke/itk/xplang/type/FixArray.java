package ppke.itk.xplang.type;

/**
 *  A fixed length array.
 */
public final class FixArray extends Type {
    private final Type indexType;
    private final Type elemType;
    private final int length;

    private FixArray(int length, Type indexType, Type elemType) {
        super("FixArray");
        this.indexType = indexType;
        this.elemType = elemType;
        this.length = length;
    }

    @Override public Type elementType() {
        return elemType;
    }

    @Override public Type indexType() {
        return indexType;
    }

    /**
     * A limited structural equivalence: a FixArray accepts other FixArrays with the same length, whose elementType
     * our elementType accepts, and whose indexType is the same as ours.
     */
    @Override public boolean accepts(Type other) {
        if(!(other instanceof FixArray)) return false;
        FixArray that = (FixArray) other;
        return this.length == that.length
            && this.indexType().equals(that.indexType())
            && this.elemType.accepts(that.elemType);
    }

    @Override public int size() {
        return this.length;
    }

    @Override public Initialization getInitialization() {
        return Initialization.ARRAY;
    }

    @Override public String toString() {
        return String.format("%d[%s]", length, elemType);
    }

    public static FixArrayBuilder of(int length, Type elemType) {
        return new FixArrayBuilder(length, elemType);
    }

    public static class FixArrayBuilder {
        private final int length;
        private final Type elemType;

        private FixArrayBuilder(int length, Type elemType) {
            this.length = length;
            this.elemType = elemType;
        }

        public FixArray indexedBy(Type indexType) {
            return new FixArray(length, indexType, elemType);
        }
    }
}
