package ppke.itk.xplang.interpreter;

/**
 * The interpreter was stopped from the outside.
 */
public class InterpreterStoppedException extends InterpreterError {
    InterpreterStoppedException() {
        super("The interpreter was stopped.");
    }
}
