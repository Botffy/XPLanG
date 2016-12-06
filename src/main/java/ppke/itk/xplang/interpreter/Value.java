package ppke.itk.xplang.interpreter;

abstract class Value {
    private static final Value NULL = new Value() {
        @Override public String toString() {
            return "DEADBEEF";
        }

        @Override public int hashCode() {
            return 0;
        }

        @Override public boolean equals(Object object) {
            return object == this;
        }
    };

    static Value nullValue() {
        return NULL;
    }

    @Override public abstract String toString();
    @Override public abstract int hashCode();
    @Override public abstract boolean equals(Object object);
}
