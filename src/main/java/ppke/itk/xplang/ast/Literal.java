package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.Type;

import java.util.Locale;

public abstract class Literal<T> extends RValue {
    private final Type type;
    private final T value;

    Literal(Location location, Type type, T value) {
        super(location);
        this.type = type;
        this.value = value;
    }

    @Override public Type getType() {
        return type;
    }

    public T getValue() {
        return value;
    }

    @Override
    public boolean isStatic() {
        return true;
    }

    @Override public String toString() {
        return String.format("%s[%s]", this.getClass().getSimpleName().toUpperCase(Locale.ENGLISH), value);
    }
}
