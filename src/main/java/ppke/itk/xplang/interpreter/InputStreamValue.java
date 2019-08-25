package ppke.itk.xplang.interpreter;

import static ppke.itk.xplang.interpreter.ValueUtils.nullValue;

class InputStreamValue implements Value {
    private ProgramInput input;

    InputStreamValue(ProgramInput input) {
        this.input = input;
    }

    Value readIntegerValue() {
        Integer val = input.readInt();
        return val == null ? nullValue() : new IntegerValue(val);
    }

    Value readRealValue() {
        Double val = input.readReal();
        return val == null ? nullValue() : new RealValue(val);
    }

    Value readCharacterValue() {
        Character val = input.readCharacter();
        return val == null ? nullValue() : new CharacterValue(val);
    }

    Value readBooleanValue() {
        Boolean val = input.readBoolean();
        return val == null ? nullValue() : BooleanValue.valueOf(val);
    }

    Value readStringValue() {
        String val = input.readLine();
        return val == null ? nullValue() : new StringValue(val);
    }

    void close() {
        input.close();
        input = null;
    }

    boolean isClosed() {
        return input == null;
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
