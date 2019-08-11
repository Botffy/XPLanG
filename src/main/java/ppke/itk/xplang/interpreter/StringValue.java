package ppke.itk.xplang.interpreter;

import java.util.Arrays;

import static ppke.itk.xplang.interpreter.ValueUtils.convert;

class StringValue implements AddressableValue, ComparableValue {
    private final char[] chars;

    StringValue(String value) {
        this.chars = value.toCharArray();
    }

    private StringValue(char[] value) {
        this.chars = value;
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
        return new StringValue(Arrays.copyOf(chars, chars.length));
    }

    @Override public int size() {
        return chars.length;
    }

    String getValue() {
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
