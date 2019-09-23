package ppke.itk.xplang.interpreter;

abstract class AddressableValue implements Value, Addressable {
    @Override
    public Value copy() {
        return this;
    }

    abstract int size();
}
