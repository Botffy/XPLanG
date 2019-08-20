package ppke.itk.xplang.interpreter;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class InputStreamValueTest {
    @Test
    public void readChar() {
        Reader input = getReader(" vőr ");
        InputStreamValue is = new InputStreamValue(input);
        assertEquals(' ', is.readCharacter());
        assertEquals('v', is.readCharacter());
        assertEquals('ő', is.readCharacter());
    }

    @Test
    public void readInt() {
        Reader input = getReader("13 -13  \t\n  67512 0 24.3");
        InputStreamValue is = new InputStreamValue(input);

        assertEquals(13, is.readInt());
        assertEquals(-13, is.readInt());
        assertEquals(67512, is.readInt());
        assertEquals(0, is.readInt());
        assertEquals(24, is.readInt());
        Throwable error = exceptionThrownBy(is::readInt);
        assertThat(error, instanceOf(InterpreterError.class));
    }

    @Test
    public void readReal() {
        Reader input = getReader("0.2 3 -823.1345   0.12345678909");
        InputStreamValue is = new InputStreamValue(input);

        assertEquals(0.2, is.readReal(),  0.0f);
        assertEquals(3.0, is.readReal(), 0.0f);
        assertEquals(-823.1345, is.readReal(), 0.0f);
        assertEquals(0.12345678909, is.readReal(), 0.0f);
    }

    @Test
    public void readLine() {
        Reader input = getReader("your day breaks\nyour mind aches\r\nyou find that all the words of kindness linger on\n\nwhen she no longer needs you");
        InputStreamValue is = new InputStreamValue(input);

        assertEquals("your day breaks", is.readLine());
        assertEquals("your mind aches", is.readLine());
        assertEquals("you find that all the words of kindness linger on", is.readLine());
        assertEquals("", is.readLine());
        assertEquals("when she no longer needs you", is.readLine());
    }

    @Test
    public void readBoolean() {
        Reader input = getReader(" ii     h y\n f F");
        InputStreamValue is = new InputStreamValue(input);

        assertEquals(true, is.readBoolean());
        assertEquals(true, is.readBoolean());
        assertEquals(false, is.readBoolean());
        assertEquals(true, is.readBoolean());
        assertEquals(false, is.readBoolean());
        assertEquals(false, is.readBoolean());
    }

    private Reader getReader(String input) {
        return new InputStreamReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
    }
}
