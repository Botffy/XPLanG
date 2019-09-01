package ppke.itk.xplang.type;

import org.junit.Test;

import static org.junit.Assert.*;

public class FixArrayNameTest {
    private static final Type TYPE_A = new Scalar("a");

    @Test
    public void arrayOfScalar() {
        FixArray array = FixArray.of(5, TYPE_A).indexedBy(Archetype.INTEGER_TYPE);
        assertEquals("a[5]", array.toString());
    }

    @Test
    public void array2D() {
        FixArray inner = FixArray.of(2, TYPE_A).indexedBy(Archetype.INTEGER_TYPE);
        FixArray array = FixArray.of(3, inner).indexedBy(Archetype.INTEGER_TYPE);

        assertEquals("a[3][2]", array.toString());
    }

    @Test
    public void array3D() {
        FixArray d1 = FixArray.of(3, TYPE_A).indexedBy(Archetype.INTEGER_TYPE);
        FixArray d2 = FixArray.of(2, d1).indexedBy(Archetype.INTEGER_TYPE);
        FixArray array = FixArray.of(1, d2).indexedBy(Archetype.INTEGER_TYPE);
        assertEquals("a[1][2][3]", array.toString());
    }
}
