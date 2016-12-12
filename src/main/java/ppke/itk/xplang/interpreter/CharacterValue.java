package ppke.itk.xplang.interpreter;

class CharacterValue extends Value {
    public final char value;

    CharacterValue(char value) {
        this.value = value;
    }

    char getValue() {
        return value;
    }

    @Override Value copy() {
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
}
