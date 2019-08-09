package ppke.itk.xplang.parser;

public class MockName implements Name {
    private final String value;

    MockName(String name) {
        this.value = name;
    }

    @Override public boolean equals(Object obj) {
        return obj instanceof MockName && this.value.equals(((MockName) obj).value);
    }

    @Override public int hashCode() {
        return value.hashCode();
    }

    @Override public String toString() {
        return value;
    }

    public static MockName name(String name) {
        return new MockName(name);
    }
}
