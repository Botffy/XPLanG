package ppke.itk.xplang.interpreter;

import java.util.function.Function;

import static ppke.itk.xplang.interpreter.ValueUtils.nullValue;

class InputStreamValue implements Value {
    private ProgramInput input;
    private boolean lastReadFailed = false;

    InputStreamValue(ProgramInput input) {
        this.input = input;
    }

    Value readIntegerValue() {
        return createValue(IntegerValue::new, input.readInt());
    }

    Value readRealValue() {
        return createValue(RealValue::new, input.readReal());
    }

    Value readCharacterValue() {
        return createValue(CharacterValue::new, input.readCharacter());
    }

    Value readBooleanValue() {
        return createValue(BooleanValue::valueOf, input.readBoolean());
    }

    Value readStringValue() {
        return createValue(StringValue::new, input.readLine());
    }

    private <T> Value createValue(Function<T, Value> valueCreator, T value) {
        if (value == null) {
            lastReadFailed = true;
            return nullValue();
        }
        return valueCreator.apply(value);
    }

    void close() {
        input.close();
        input = null;
        lastReadFailed = false;
    }

    boolean isClosed() {
        return input == null;
    }

    /**
     * The stream is exhausted, the next read operation will fail.
     */
    boolean isExhausted() {
        if (isClosed()) {
            throw new UnopenedStreamException();
        }
        return input.isExhausted();
    }

    /**
     * There has been at least one unsuccessful read operation.
     */
    boolean hasBeenExhausted() {
        if (isClosed()) {
            throw new UnopenedStreamException();
        }
        return lastReadFailed;
    }

    @Override
    public Value copy() {
        return new InputStreamValue(input);
    }

    @Override
    public String toString() {
        return isClosed() ? "ClosedInputStream" : String.format("OpenInputStream(%s)", input.getName());
    }
}
