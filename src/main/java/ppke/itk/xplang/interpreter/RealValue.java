package ppke.itk.xplang.interpreter;

import java.text.DecimalFormat;
import java.util.Locale;

public class RealValue implements ComparableValue, WritableValue, ScalarValue<Double> {
    private static final DecimalFormat format = DecimalFormatFactory.realDecimalFormat();

    private final double value;

    RealValue(double value) {
        this.value = value;
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override public RealValue copy() {
        return this;
    }

    @Override
    public String asOutputString() {
        return format.format(this.value);
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

    @Override
    public int compareTo(Value other) {
        if (!(other instanceof RealValue)) {
            throw new IllegalStateException("Can only compare values of the same type.");
        }

        RealValue that = (RealValue) other;
        return Double.compare(this.value, that.value);
    }
}
