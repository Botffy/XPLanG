package ppke.itk.xplang.interpreter;

public class UnopenedStreamException extends InterpreterError {
    UnopenedStreamException() {
        super("The stream is not open.");
    }
}
