package ppke.itk.xplang.interpreter;

import java.util.Arrays;

import static ppke.itk.xplang.interpreter.ValueUtils.convert;

class StringValue extends IntegerAddressable implements ComparableValue, SlicableValue, WritableValue, ScalarValue<String> {
    private final char[] chars;

    StringValue(String value) {
        this.chars = value.toCharArray();
    }

    private StringValue(char[] value) {
        this.chars = value;
    }

    @Override
    public void set(int index, Value value) throws InterpreterError {
        chars[index] = convert(value, CharacterValue.class).getValue();
    }

    @Override
    public CharacterValue get(int index) throws InterpreterError {
        return new CharacterValue(chars[index]);
    }

    @Override
    public StringValue copy() {
        return new StringValue(Arrays.copyOf(chars, chars.length));
    }

    @Override
    public String asOutputString() {
        return new String(this.chars);
    }

    @Override public int size() {
        return chars.length;
    }

    @Override
    public String getValue() {
        return new String(chars);
    }

    StringValue append(CharacterValue character) {
        char[] n = new char[this.chars.length + 1];
        System.arraycopy(this.chars, 0, n, 0, this.chars.length);
        n[this.chars.length] = character.value;
        return new StringValue(n);
    }

    StringValue prepend(CharacterValue character) {
        char[] n = new char[this.chars.length + 1];
        System.arraycopy(this.chars, 0, n, 1, this.chars.length);
        n[0] = character.value;
        return new StringValue(n);
    }

    StringValue concat(StringValue that) {
        char[] n = new char[this.chars.length + that.chars.length];
        System.arraycopy(this.chars, 0, n, 0, this.chars.length);
        System.arraycopy(that.chars, 0, n, this.chars.length, that.chars.length);
        return new StringValue(n);
    }

    IntegerValue findSubstring(StringValue that) {
        int i = this.getValue().indexOf(that.getValue());
        if (i == -1) {
            i = this.chars.length;
        }
        return new IntegerValue(i);
    }

    IntegerValue findCharacter(CharacterValue that) {
        char value = that.getValue();
        int i;
        for (i = 0; i < chars.length; ++i) {
            if (chars[i] == value) {
                break;
            }
        }
        return new IntegerValue(i);
    }

    @Override
    public StringValue getSlice(IntegerValue from, IntegerValue to) throws InterpreterError {
        if (from.getValue() < 0 || from.getValue() > this.chars.length) {
            throw new InterpreterError(ErrorCode.ILLEGAL_START_INDEX, from.getValue());
        }
        if (to.getValue() < 0 || to.getValue() > this.chars.length) {
            throw new InterpreterError(ErrorCode.ILLEGAL_END_INDEX, to.getValue());
        }
        char[] n = Arrays.copyOfRange(this.chars, from.getValue(), to.getValue());
        return new StringValue(n);
    }

    @Override public String toString() {
        return String.format("String(%s)", new String(this.chars).replace("\n", "\\n"));
    }

    @Override public int hashCode() {
        return Arrays.hashCode(chars);
    }

    @Override public boolean equals(Object object) {
        return object instanceof StringValue && Arrays.equals(this.chars, ((StringValue) object).chars);
    }

    @Override
    public int compareTo(Value other) {
        if (!(other instanceof StringValue) ) {
            throw new IllegalStateException("Can only compare values of the same type.");
        }

        StringValue that = (StringValue) other;
        return new String(this.chars).compareTo(new String(that.chars));
    }
}
