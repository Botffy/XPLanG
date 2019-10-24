package ppke.itk.xplang.type;

/**
 * A type associated with a value.
 */
public abstract class Type {
    private final String label;

    protected Type(String label) {
        this.label = label;
    }

    /**
     * Would this type accept that; would that thing fit into this hole?
     *
     * The ACCEPTS(T1,T2) is a fundamental property of the type system. It determines eligibility for assignment, but
     * also determines whether a function accepts a certain value as parameter, etc.
     *
     * This can be also viewed as a subtyping relationship, T2&lt;=T1. If T22&lt;=T1 and T12&lt;=T1, that implies
     * equivalence, making the type system a partial order.
     *
     * @param that The type we compare to ourselves. Would we accept that value as ours?
     * @return true if this accepts that, false otherwise.
     */
    public abstract boolean accepts(Type that);

    /**
     *  The type of the components of this type, or {@link Archetype#NONE} if the type is scalar.
     */
    public Type elementType() {
        return Archetype.NONE;
    }

    /**
     *  Returns the type of the indexing elements of this type, or {@link Archetype#NONE} if the type is a scalar.
     */
    public Type indexType() {
        return Archetype.NONE;
    }

    /**
     * The dimensions of this type. 0 for scalars.
     */
    public int size() {
        return 0;
    }

    public Initialization getInitialization() {
        return Initialization.SCALAR;
    }

    public String getLabel() {
        return label;
    }

    @Override public String toString() {
        return label;
    }

    public enum Initialization {
        SCALAR,
        ARRAY,
        RECORD,
        INPUT_STREAM,
        OUTPUT_STREAM
    }
}
