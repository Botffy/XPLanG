package ppke.itk.xplang.interpreter;

import java.util.Arrays;

import static ppke.itk.xplang.interpreter.ValueUtils.convert;

class StringValue implements AddressableValue, ComparableValue {
    private final char[] chars;

    StringValue(String value) {
        this.chars = value.toCharArray();
    }

    StringValue(char[] value) {
        this.chars = Arrays.copyOf(value, value.length);
    }

    @Override public ReferenceValue getReference(Object address) throws InterpreterError {
        return new ComponentReference(this, address);
    }

    @Override public void setComponent(Object index, Value value) throws InterpreterError {
        chars[convert(index, IntegerValue.class).getValue()] = convert(value, CharacterValue.class).getValue();
    }

    @Override public CharacterValue getComponent(Object index) throws InterpreterError {
        return new CharacterValue(chars[convert(index, IntegerValue.class).getValue()]);
    }

    @Override public StringValue copy() {
        return new StringValue(chars);
    }

    @Override public int size() {
        return chars.length;
    }

    @Override public String toString() {
        return String.format("String(%s)", new String(this.chars));
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
            throw new InterpreterError("Can only compare values of the same type.");
        }

        StringValue that = (StringValue) other;
        return new String(this.chars).compareTo(new String(that.chars));
    }
}
