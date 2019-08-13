package ppke.itk.xplang.type;

public class AddressableScalar extends Scalar {
    private final Type elementType;
    private final Scalar indexType;

    public AddressableScalar(String label, Type elementType, Scalar indexType) {
        this(label, elementType, indexType, Archetype.NONE);
    }

    public AddressableScalar(String label, Type elementType, Scalar indexType, Type superType) {
        super(label, superType);
        this.elementType = elementType;
        this.indexType = indexType;
    }

    @Override
    public Type elementType() {
        return elementType;
    }

    @Override
    public Type indexType() {
        return indexType;
    }
}
