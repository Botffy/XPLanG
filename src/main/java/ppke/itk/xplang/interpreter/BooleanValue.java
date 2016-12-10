package ppke.itk.xplang.interpreter;

class BooleanValue extends Value {
    private final boolean value;

    BooleanValue(boolean value) {
        this.value = value;
    }

    boolean getValue() {
        return value;
    }

    @Override public String toString() {
        return String.format("Boolean(%s)", value);
    }

    @Override public int hashCode() {
        return Boolean.hashCode(value);
    }

    @Override public boolean equals(Object object) {
        return object instanceof BooleanValue && this.value == ((BooleanValue) object).value;
    }
}
