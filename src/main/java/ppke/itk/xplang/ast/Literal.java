package ppke.itk.xplang.ast;

import java.util.Locale;

public abstract class Literal<T> extends RValue {
    private final T value;

    Literal(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override public String toString() {
        return String.format("%s[%s]", this.getClass().getSimpleName().toUpperCase(Locale.ENGLISH), value);
    }
}
