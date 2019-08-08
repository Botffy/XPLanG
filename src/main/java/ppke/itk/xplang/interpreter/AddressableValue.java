package ppke.itk.xplang.interpreter;

interface AddressableValue extends Value, Addressable {
    @Override
    public default Value copy() {
        return this;
    }

    int size();
}
