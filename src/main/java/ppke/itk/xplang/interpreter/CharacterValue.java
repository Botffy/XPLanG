package ppke.itk.xplang.interpreter;

class CharacterValue implements ComparableValue {
    public final char value;

    CharacterValue(char value) {
        this.value = value;
    }

    char getValue() {
        return value;
    }

    @Override public Value copy() {
        return this;
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
            throw new InterpreterError("Can only compare values of the same type.");
        }

        CharacterValue that = (CharacterValue) other;
        return Character.compare(this.value, that.value);
    }
}
