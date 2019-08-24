package ppke.itk.xplang.type;

public class Scalar extends Type {
    private final Type superType;
    private final Initialization initialization;

    public Scalar(String label) {
        this(label, null, Initialization.SCALAR);
    }

    public Scalar(String label, Type superType) {
        this(label, superType, Initialization.SCALAR);
    }

    public Scalar(String label, Initialization initialization) {
        this(label, null, initialization);
    }

    public Scalar(String label, Type superType, Initialization initialization) {
        super(label);
        this.superType = superType;
        this.initialization = initialization;
    }

    /**
     * A Scalar is equal to another if they are represented by the same object. This is something of a nominative
     * equivalence, though a Grammar may register the same type under different aliases.
     */
    @Override public boolean accepts(Type that) {
        if (this == that) {
            return true;
        }

        if (that instanceof Scalar) {
            Scalar scalar = (Scalar) that;
            return this.equals(scalar.superType);
        }

        return false;
    }

    @Override public int size() {
        return 0;
    }

    @Override public Initialization getInitialization() {
        return initialization;
    }
}
