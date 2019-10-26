package ppke.itk.xplang.interpreter;

final class BooleanValue implements WritableValue, ComparableValue, ScalarValue<Boolean> {
    final static BooleanValue TRUE = new BooleanValue(true);
    final static BooleanValue FALSE = new BooleanValue(false);

    private final boolean value;

    private BooleanValue(boolean value) {
        this.value = value;
    }

     @Override
    public Boolean getValue() {
        return value;
    }

    @Override public BooleanValue copy() {
        return this;
    }

    @Override
    public String asOutputString() {
        return this.value ? "igaz" : "hamis";
    }

    public BooleanValue negate() {
        return this == TRUE ? FALSE : TRUE;
    }

    public BooleanValue or(BooleanValue that) {
        return valueOf(this.value || that.value);
    }

    public BooleanValue and(BooleanValue that) {
        return valueOf(this.value && that.value);
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

    @Override
    public int compareTo(Value other) {
        if (!(other instanceof BooleanValue)) {
            throw new IllegalStateException("Can only compare values of the same type.");
        }

        BooleanValue that = (BooleanValue) other;
        return Boolean.compare(this.value, that.value);
    }
}
