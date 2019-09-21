package ppke.itk.xplang.interpreter;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Thrown when the {@link Interpreter} cannot finish the execution of a program.
 *
 * <p>These are expected, legal error states from the point of view of the {@code Interpreter}: callers are expected to
 * catch and handle them. From the point of view of the interpreted program, however, these are illegal states.</p>
 *
 * <p>InterpreterErrors contain an {@link ErrorCode} and a finite list of parameters for that error code. Callers
 * are expected to construct localised error messages from these.</p>
 *
 * <p>When the {@code Interpreter} itself finds itself in an illegal state, an {@link IllegalStateException} is
 * thrown.</p>
 */
public class InterpreterError extends RuntimeException {
    private final ErrorCode errorCode;
    private final List<Object> params;

    InterpreterError(ErrorCode errorCode, Object... params) {
        super("Interpreter error " + errorCode + asList(params));
        this.errorCode = errorCode;
        this.params = asList(params);
    }

    InterpreterError(ErrorCode errorCode, Throwable cause, Object... params) {
        super("Interpreter error " + errorCode + asList(params), cause);
        this.errorCode = errorCode;
        this.params = asList(params);
    }


    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public List<Object> getParams() {
        return params;
    }
}
