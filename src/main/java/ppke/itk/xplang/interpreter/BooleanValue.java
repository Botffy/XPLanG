package ppke.itk.xplang.interpreter;

final class BooleanValue implements Value {
    final static BooleanValue TRUE = new BooleanValue(true);
    final static BooleanValue FALSE = new BooleanValue(false);

    private final boolean value;

    private BooleanValue(boolean value) {
        this.value = value;
    }

    boolean getValue() {
        return value;
    }

    @Override public BooleanValue copy() {
        return this;
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

    static BooleanValue valueOf(boolean value) {
        return value? TRUE : FALSE;
    }
}
