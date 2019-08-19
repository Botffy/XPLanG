package ppke.itk.xplang.interpreter;

import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;

class IntegerValue implements ComparableValue, WritableValue {
    private static final DecimalFormat format = new DecimalFormat();

    private final int value;

    IntegerValue(int value) {
        this.value = value;
    }

    int getValue() {
        return value;
    }

    @Override public IntegerValue copy() {
        return this;
    }

    @Override
    public void writeTo(Writer writer) throws IOException {
        writer.write(format.format(value));
        writer.flush();
    }

    @Override public String toString() {
        return String.format("Integer(%s)", value);
    }

    @Override public int hashCode() {
        return value;
    }

    @Override public boolean equals(Object object) {
        return object instanceof IntegerValue && this.value == ((IntegerValue) object).value;
    }

    @Override
    public int compareTo(Value other) {
        if (!(other instanceof IntegerValue)) {
            throw new InterpreterError("Can only compare values of the same type.");
        }

        IntegerValue that = (IntegerValue) other;
        return Integer.compare(this.value, that.value);
    }
}
