package ppke.itk.xplang.interpreter;

import ppke.itk.xplang.type.Type;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A value the {@link Interpreter} works on.
 */
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

    static Value initialise(Type type) {
        if(!type.isScalar()) {
            return new ArrayValue(
                Stream.generate(() -> initialise(type.elementType())).limit(type.size()).collect(Collectors.toList())
            );
        }
        return NULL;
    }

    @Override public abstract String toString();
    @Override public abstract int hashCode();
    @Override public abstract boolean equals(Object object);
}
