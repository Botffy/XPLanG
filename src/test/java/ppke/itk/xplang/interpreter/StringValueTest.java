package ppke.itk.xplang.interpreter;

import org.junit.Test;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class StringValueTest {
    @Test
    public void getCharacterByNegativeIndex() {
        StringValue stringValue = new StringValue("hi");
        Throwable error = exceptionThrownBy(() -> stringValue.getComponent(new IntegerValue(-1)));
        assertThat(error, instanceOf(InterpreterError.class));
        assertEquals(ErrorCode.ILLEGAL_INDEX, ((InterpreterError) error).getErrorCode());
    }

    @Test
    public void getCharacterByTooLargeIndex() {
        StringValue stringValue = new StringValue("hi");
        Throwable error = exceptionThrownBy(() -> stringValue.getComponent(new IntegerValue(10)));
        assertThat(error, instanceOf(InterpreterError.class));
        assertEquals(ErrorCode.ILLEGAL_INDEX, ((InterpreterError) error).getErrorCode());
    }

    @Test
    public void setCharacterByNegativeIndex() {
        StringValue stringValue = new StringValue("hi");
        Throwable error = exceptionThrownBy(() -> stringValue.setComponent(new IntegerValue(-1), new CharacterValue('a')));
        assertThat(error, instanceOf(InterpreterError.class));
        assertEquals(ErrorCode.ILLEGAL_INDEX, ((InterpreterError) error).getErrorCode());
    }

    @Test
    public void setCharacterByTooLargeIndex() {
        StringValue stringValue = new StringValue("hi");
        Throwable error = exceptionThrownBy(() -> stringValue.setComponent(new IntegerValue(10), new CharacterValue('a')));
        assertThat(error, instanceOf(InterpreterError.class));
        assertEquals(ErrorCode.ILLEGAL_INDEX, ((InterpreterError) error).getErrorCode());
    }

}
