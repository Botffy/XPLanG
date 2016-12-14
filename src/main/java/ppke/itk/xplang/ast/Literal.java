package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;

import java.util.Locale;

public abstract class Literal<T> extends RValue {
    private final T value;

    Literal(Location location, T value) {
        super(location);
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override public String toString() {
        return String.format("%s[%s]", this.getClass().getSimpleName().toUpperCase(Locale.ENGLISH), value);
    }
}
