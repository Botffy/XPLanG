package ppke.itk.xplang.interpreter;

public class ExecutionLimitReachedException extends InterpreterError {
    ExecutionLimitReachedException(int maxSteps) {
        super(String.format("The maximal number of statements to execute (%s) was reached.", maxSteps));
    }
}
