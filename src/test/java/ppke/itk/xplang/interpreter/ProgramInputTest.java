package ppke.itk.xplang.interpreter;

import org.junit.Test;
import ppke.itk.xplang.ast.Program;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class ProgramInputTest {
    @Test
    public void readChar() {
        Reader input = getReader(" vőr ");
        ProgramInput is = new ProgramInput(input, "test");
        assertEquals(' ', (char) is.readCharacter());
        assertEquals('v', (char) is.readCharacter());
        assertEquals('ő', (char) is.readCharacter());
    }

    @Test
    public void readInt() {
        Reader input = getReader("13 -13  \t\n  67512 0 24.3");
        ProgramInput is = new ProgramInput(input, "test");

        assertEquals(13, (int) is.readInt());
        assertEquals(-13, (int) is.readInt());
        assertEquals(67512, (int) is.readInt());
        assertEquals(0, (int) is.readInt());
        assertEquals(24, (int) is.readInt());
        Throwable error = exceptionThrownBy(is::readInt);
        assertThat(error, instanceOf(InterpreterError.class));
    }

    @Test
    public void readReal() {
        Reader input = getReader("0.2 3 -823.1345   0.12345678909");
        ProgramInput is = new ProgramInput(input, "test");

        assertEquals(0.2, is.readReal(),  0.0f);
        assertEquals(3.0, is.readReal(), 0.0f);
        assertEquals(-823.1345, is.readReal(), 0.0f);
        assertEquals(0.12345678909, is.readReal(), 0.0f);
    }

    @Test
    public void readLine() {
        Reader input = getReader("your day breaks\nyour mind aches\r\nyou find that all the words of kindness linger on\n\nwhen she no longer needs you");
        ProgramInput is = new ProgramInput(input, "test");

        assertEquals("your day breaks", is.readLine());
        assertEquals("your mind aches", is.readLine());
        assertEquals("you find that all the words of kindness linger on", is.readLine());
        assertEquals("", is.readLine());
        assertEquals("when she no longer needs you", is.readLine());
    }

    @Test
    public void oneCharacter() {
        Reader input = getReader("a");
        ProgramInput is = new ProgramInput(input, "test");
        assertEquals("a", is.readLine());
    }

    @Test
    public void readBoolean() {
        Reader input = getReader(" ii     h y\n f F");
        ProgramInput is = new ProgramInput(input, "test");

        assertEquals(true, is.readBoolean());
        assertEquals(true, is.readBoolean());
        assertEquals(false, is.readBoolean());
        assertEquals(true, is.readBoolean());
        assertEquals(false, is.readBoolean());
        assertEquals(false, is.readBoolean());
    }

    @Test
    public void readFromExhaustedStream() {
        Reader input = getReader("");
        ProgramInput is = new ProgramInput(input, "test");

        assertNull(is.readBoolean());
        assertNull(is.readInt());
        assertNull(is.readLine());
        assertNull(is.readReal());
        assertNull(is.readCharacter());
    }

    private Reader getReader(String input) {
        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))));
    }
}
