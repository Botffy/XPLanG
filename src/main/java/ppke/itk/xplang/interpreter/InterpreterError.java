package ppke.itk.xplang.interpreter;

public class InterpreterError extends RuntimeException {
    InterpreterError(String message) {
        super(message);
    }
}
