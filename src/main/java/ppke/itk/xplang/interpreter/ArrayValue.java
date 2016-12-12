package ppke.itk.xplang.interpreter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;

class ArrayValue extends AddressableValue {
    private final static Logger log = LoggerFactory.getLogger("Root.Interpreter");

    private final List<Value> values;

    ArrayValue(List<Value> initValues) {
        values = new ArrayList<>();
        values.addAll(initValues);
    }

    @Override public ReferenceValue getReference(Object address) throws InterpreterError {
        return new ComponentReference(this, address);
    }

    @Override public void setComponent(Object index, Value value) throws InterpreterError {
        values.set(toAddressValue(index).getValue(), value);
    }

    @Override public Value getComponent(Object index) throws InterpreterError {
        return values.get(toAddressValue(index).getValue());
    }

    private IntegerValue toAddressValue(Object index) throws InterpreterError {
        if(!(index instanceof IntegerValue)) {
            log.error("Could cast {} into an IntegerValue", index);
            throw new InterpreterError(String.format("Could not cast %s into an IntegerValue", index));
        }

        return (IntegerValue) index;
    }

    @Override public String toString() {
        return String.format("Array(%s)", values.stream().map(Value::toString).collect(joining(",")));
    }

    @Override public int hashCode() {
        return values.hashCode();
    }

    @Override public boolean equals(Object that) {
        return this == that;
    }
}
