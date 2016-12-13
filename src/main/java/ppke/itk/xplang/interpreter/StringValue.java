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
        chars[toAddressValue(index).getValue()] = ((CharacterValue) value).getValue();
    }

    @Override public CharacterValue getComponent(Object index) throws InterpreterError {
        return new CharacterValue(chars[toAddressValue(index).getValue()]);
    }

    private IntegerValue toAddressValue(Object index) throws InterpreterError {
        if(!(index instanceof IntegerValue)) {
            throw new InterpreterError(String.format("Could not cast %s into an IntegerValue", index));
        }

        return (IntegerValue) index;
    }

    @Override StringValue copy() {
        return new StringValue(chars);
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
