package ppke.itk.xplang.interpreter;

public interface SlicableValue extends Value {
    SlicableValue getSlice(IntegerValue from, IntegerValue to) throws InterpreterError;
}
