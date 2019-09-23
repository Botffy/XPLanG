package ppke.itk.xplang.interpreter;

import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ArrayValueTest {
    @Test
    public void getElementByNegativeIndex() {
        ArrayValue array = arrayOf(2);

        Throwable error = exceptionThrownBy(() -> array.getComponent(new IntegerValue(-1)));
        assertThat(error, instanceOf(InterpreterError.class));
        assertEquals(ErrorCode.ILLEGAL_INDEX, ((InterpreterError) error).getErrorCode());
    }

    @Test
    public void getElementByTooLargeIndex() {
        ArrayValue array = arrayOf(2);

        Throwable error = exceptionThrownBy(() -> array.getComponent(new IntegerValue(5)));
        assertThat(error, instanceOf(InterpreterError.class));
        assertEquals(ErrorCode.ILLEGAL_INDEX, ((InterpreterError) error).getErrorCode());
    }

    @Test
    public void setElementByNegativeIndex() {
        ArrayValue array = arrayOf(2);

        Throwable error = exceptionThrownBy(() -> array.setComponent(new IntegerValue(-1), new IntegerValue(6)));
        assertThat(error, instanceOf(InterpreterError.class));
        assertEquals(ErrorCode.ILLEGAL_INDEX, ((InterpreterError) error).getErrorCode());
    }

    @Test
    public void setElementByTooLargeIndex() {
        ArrayValue array = arrayOf(2);

        Throwable error = exceptionThrownBy(() -> array.setComponent(new IntegerValue(5), new IntegerValue(100)));
        assertThat(error, instanceOf(InterpreterError.class));
        assertEquals(ErrorCode.ILLEGAL_INDEX, ((InterpreterError) error).getErrorCode());
    }

    private static ArrayValue arrayOf(int length) {
        return new ArrayValue(IntStream.of(length).mapToObj(IntegerValue::new).collect(Collectors.toList()));
    }
}
