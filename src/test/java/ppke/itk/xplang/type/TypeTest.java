package ppke.itk.xplang.type;

import org.junit.Test;

import static org.junit.Assert.*;

public class TypeTest {
    @Test
    public void subtyping() {
        Type a = new Scalar("a");
        Type b = new Scalar("b", a);

        assertTrue(a.accepts(b));
    }
}
