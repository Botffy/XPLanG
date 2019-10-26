package ppke.itk.xplang.interpreter;

class CharacterValue implements ComparableValue, WritableValue, ScalarValue<Character> {
    public final char value;

    CharacterValue(char value) {
        this.value = value;
    }

    @Override
    public Character getValue() {
        return value;
    }

    @Override public Value copy() {
        return this;
    }

    @Override
    public String asOutputString() {
        return String.valueOf(this.value);
    }

    @Override public String toString() {
        return String.format("Character(%s)", value);
    }

    @Override public int hashCode() {
        return Character.hashCode(value);
    }

    @Override public boolean equals(Object object) {
        return object instanceof CharacterValue && this.value == ((CharacterValue) object).value;
    }

    @Override
    public int compareTo(Value other) {
        if (!(other instanceof CharacterValue)) {
            throw new IllegalStateException("Can only compare values of the same type.");
        }

        CharacterValue that = (CharacterValue) other;
        return Character.compare(this.value, that.value);
    }
}
