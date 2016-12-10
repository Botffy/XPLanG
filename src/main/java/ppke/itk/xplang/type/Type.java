package ppke.itk.xplang.type;

/**
 * A type associated with a value.
 */
public abstract class Type {
    private final String label;

    protected Type(String label) {
        this.label = label;
    }

    @Override public String toString() {
        return label;
    }
}
