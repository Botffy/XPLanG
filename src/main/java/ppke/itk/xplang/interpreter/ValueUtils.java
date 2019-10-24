package ppke.itk.xplang.interpreter;

import ppke.itk.xplang.type.RecordType;
import ppke.itk.xplang.type.Type;

import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class ValueUtils {
    private static final Value NULL = new Value() {
        @Override public Value copy() {
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
        switch (type.getInitialization()) {
            case INPUT_STREAM:
                return new InputStreamValue(null);
            case OUTPUT_STREAM:
                return new OutputStreamValue();
            case ARRAY:
                return new ArrayValue(
                    Stream.generate(() -> initialise(type.elementType())).limit(type.size()).collect(toList())
                );
            case RECORD: {
                RecordType recordType = (RecordType) type;
                return new RecordValue(
                    recordType.getFields()
                        .stream()
                        .collect(toMap(
                            x -> x.getName().toString(),
                            x -> initialise(x.getType())
                        ))
                );
            }
            default:
                return NULL;
        }
    }

    /**
     * Attempt to cast a value to a specific value type, or die trying.
     */
    static <T extends Value> T convert(Object value, Class<T> clazz) throws InterpreterError {
        try {
            return clazz.cast(value);
        } catch(ClassCastException cce) {
            throw new IllegalStateException(
                String.format(
                    "Type error: could not convert %s to %s", value.getClass().getSimpleName(), clazz.getSimpleName()
                ), cce
            );
        }
    }
}
