package ppke.itk.xplang.interpreter;

import java.util.Arrays;

class StringValue extends AddressableValue {
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

    @Override StringValue copy() {
        return new StringValue(chars);
    }

    @Override int size() {
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
}
