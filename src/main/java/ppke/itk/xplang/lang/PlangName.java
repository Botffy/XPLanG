package ppke.itk.xplang.lang;

import ppke.itk.xplang.parser.Name;

import java.util.Locale;

/**
 * The Name class for the PLanG-strict language. Case-insensitive.
 */
class PlangName implements Name {
    private final String value;

    PlangName(String str) {
        this.value = str.toLowerCase(Locale.ENGLISH);
    }

    @Override public boolean equals(Object obj) {
        return obj instanceof PlangName && ((PlangName) obj).value.equals(this.value);
    }

    @Override public int hashCode() {
        return value.hashCode();
    }

    @Override public String toString() {
        return value;
    }

    static PlangName name(String name) {
        return new PlangName(name);
    }
}
