package ppke.itk.xplang.interpreter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static ppke.itk.xplang.interpreter.ValueUtils.convert;

class ArrayValue implements SlicableValue, AddressableValue, WritableValue {
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
        values.set(convert(index, IntegerValue.class).getValue(), value);
    }

    /**
     * Get the value stored at a specific slot of the array.
     * @param indexValue The index to be queried.
     * @return The value stored at the given index in the array.
     * @throws InterpreterError with errorCode NULL_ERROR if the returned value would be null.
     */
    @Override public Value getComponent(Object indexValue) throws InterpreterError {
        int index;
        try {
            index = convert(indexValue, IntegerValue.class).getValue();
        } catch (ClassCastException e) {
            throw new IllegalStateException(e);
        }

        Value value = values.get(index);
        if (value == ValueUtils.nullValue()) {
            throw new InterpreterError(ErrorCode.NULL_ERROR);
        }

        return value;
    }

    @Override
    public ArrayValue getSlice(IntegerValue start, IntegerValue end) throws InterpreterError {
        return new ArrayValue(values.subList(start.getValue(), end.getValue()));
    }

    @Override public ArrayValue copy() {
        return new ArrayValue(values.stream().map(Value::copy).collect(toList()));
    }

    @Override
    public String asOutputString() {
        return values.stream()
            .map(x -> (WritableValue) x)
            .map(WritableValue::asOutputString)
            .collect(joining(" "));
    }

    @Override public int size() {
        return values.size();
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
