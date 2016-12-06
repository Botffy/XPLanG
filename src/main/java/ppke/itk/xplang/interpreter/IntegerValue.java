package ppke.itk.xplang.interpreter;

class IntegerValue extends Value {
    private final int value;

    IntegerValue(int value) {
        this.value = value;
    }

    int getValue() {
        return value;
    }

    @Override public String toString() {
        return String.format("[Integer value %s]", 5);
    }

    @Override public int hashCode() {
        return value;
    }

    @Override public boolean equals(Object object) {
        return object instanceof IntegerValue && this.value == ((IntegerValue) object).value;
    }
}
