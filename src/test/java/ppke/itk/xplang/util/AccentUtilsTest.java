package ppke.itk.xplang.util;

import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;
import static ppke.itk.xplang.util.AccentUtils.calculateVariants;

public class AccentUtilsTest {
    @Test
    public void noAccents() {
        Set<String> result = calculateVariants("hello");
        assertEquals(1, result.size());
        assertTrue("Should contain the original string", result.contains("hello"));
    }

    @Test
    public void oneAccentedCharacter() {
        Set<String> result = calculateVariants("egész");
        System.out.println(result);
        assertEquals(2, result.size());
        assertTrue("Should contain the original string", result.contains("egész"));
        assertTrue("Should contain 'egesz'", result.contains("egesz"));
    }

    @Test
    public void twoCharacters() {
        Set<String> result = calculateVariants("különben");
        System.out.println(result);
        assertEquals(4, result.size());
        assertTrue("Should contain the original string", result.contains("különben"));
        assertTrue("Should contain the version with no accents", result.contains("kulonben"));
        assertTrue("Should contain a mixed case", result.contains("külonben"));
        assertTrue("Should contain the other mixed case", result.contains("kulönben"));
    }

    @Test
    public void twoCharactersDoubleAcute() {
        Set<String> result = calculateVariants("űrék");
        System.out.println(result);
        assertEquals(10, result.size());
        assertTrue("ü should be accepted for ű", result.contains("ürek"));
        assertTrue("ü should be accepted for ű", result.contains("ürék"));
    }

    @Test
    public void threeCharacters() {
        Set<String> result = calculateVariants("ééé");
        System.out.println(result);
        assertEquals(8, result.size());
    }
}
