package ppke.itk.xplang.interpreter;

public class BadInputException extends InterpreterError {
    BadInputException() {
        super("Failed to read from input");
    }

    BadInputException(Throwable cause) {
        super("Failed to read from input", cause);
    }
}
