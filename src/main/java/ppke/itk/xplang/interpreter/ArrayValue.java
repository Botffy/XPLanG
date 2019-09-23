package ppke.itk.xplang.interpreter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

class ArrayValue extends IntegerAddressable implements SlicableValue, WritableValue {
    private final static Logger log = LoggerFactory.getLogger("Root.Interpreter");

    private final List<Value> values;

    ArrayValue(List<Value> initValues) {
        values = new ArrayList<>();
        values.addAll(initValues);
    }

    @Override
    public void set(int index, Value value) throws InterpreterError {
        values.set(index, value);
    }

    @Override
    public Value get(int index) throws InterpreterError {
        return values.get(index);
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

