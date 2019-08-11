package ppke.itk.xplang.interpreter;

public interface SlicableValue extends AddressableValue {
    SlicableValue getSlice(IntegerValue from, IntegerValue to) throws InterpreterError;
}
