package ppke.itk.xplang.parser;

import java.util.Objects;

public class OperatorName implements Name {
    private final String label;

    public OperatorName(String label) {
        this.label = label;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OperatorName)) {
            return false;
        }

        OperatorName that = (OperatorName) obj;
        return this.label.equals(that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash("#Operator$" + label);
    }

    @Override
    public String toString() {
        return "Operator#" + label;
    }

    public static OperatorName operator(String label) {
        return new OperatorName(label);
    }
}
