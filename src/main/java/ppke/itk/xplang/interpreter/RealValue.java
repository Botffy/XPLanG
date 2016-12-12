package ppke.itk.xplang.interpreter;

import java.util.Locale;

public class RealValue extends Value {
    private final double value;

    RealValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override RealValue copy() {
        return this;
    }

    @Override public String toString() {
        return String.format(Locale.ENGLISH, "Real(%.4f)", value);
    }

    @Override public int hashCode() {
        return Double.hashCode(value);
    }

    @Override public boolean equals(Object object) {
        return object instanceof RealValue && this.value == ((RealValue) object).value;
    }
}
