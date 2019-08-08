package ppke.itk.xplang.interpreter;

class IntegerValue implements ComparableValue {
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
