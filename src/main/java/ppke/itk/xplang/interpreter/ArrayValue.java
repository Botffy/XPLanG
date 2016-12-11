package ppke.itk.xplang.interpreter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.joining;

class ArrayValue extends Value {
    private final List<Value> values;

    ArrayValue(int size) {
        values = new ArrayList<>();
        values.addAll(Collections.nCopies(size, Value.nullValue()));
    }

    @Override public String toString() {
        return String.format("Array(%s)", values.stream().map(Value::toString).collect(joining(",")));
    }

    @Override public int hashCode() {
        return 0;
    }

    @Override public boolean equals(Object object) {
        return false;
    }
}
