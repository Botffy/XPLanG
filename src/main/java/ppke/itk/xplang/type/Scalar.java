package ppke.itk.xplang.type;

public class Scalar extends Type {
    private final Type superType;

    public Scalar(String label) {
        this(label, null);
    }

    public Scalar(String label, Type superType) {
        super(label);
        this.superType = superType;
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
        return Initialization.SCALAR;
    }
}
