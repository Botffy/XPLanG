package ppke.itk.xplang.interpreter;

abstract class AddressableValue extends Value implements Addressable {
    @Override Value copy() {
        return this;
    }
}
