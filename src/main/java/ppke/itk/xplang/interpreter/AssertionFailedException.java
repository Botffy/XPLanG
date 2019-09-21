package ppke.itk.xplang.interpreter;

class AssertionFailedException extends InterpreterError {
    AssertionFailedException(StringValue message) {
        super(message.getValue());
    }
}
