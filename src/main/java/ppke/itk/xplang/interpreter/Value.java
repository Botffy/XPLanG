package ppke.itk.xplang.interpreter;

import ppke.itk.xplang.type.Type;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A value the {@link Interpreter} works on.
 */
abstract class Value {
    private static final Value NULL = new Value() {
        @Override Value copy() {
            return this;
        }

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

    static Value initialise(Type type) {
        if(!type.isScalar()) {
            return new ArrayValue(
                Stream.generate(() -> initialise(type.elementType())).limit(type.size()).collect(Collectors.toList())
            );
        }
        return NULL;
    }

    /**
     * Attempt to cast a value to a specific value type, or die trying.
     */
    static <T extends Value> T convert(Object value, Class<T> clazz) throws InterpreterError {
        try {
            return clazz.cast(value);
        } catch(ClassCastException cce) {
            throw new InterpreterError(
                String.format(
                    "Type error: could not convert %s to %s", value.getClass().getSimpleName(), clazz.getSimpleName()
                ), cce
            );
        }
    }

    abstract Value copy();
    @Override public abstract String toString();
    @Override public abstract int hashCode();
    @Override public abstract boolean equals(Object object);
}
