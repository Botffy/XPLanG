package ppke.itk.xplang.interpreter;

/**
 * Something that stores Values addressed by Values. Like an array (stores whatever types, addressed by
 * IntegerValues) or a record (stores whatever types, addressed by StringValues)
 */
interface Addressable {
    ReferenceValue getReference(Value address) throws InterpreterError;
    void setComponent(Value address, Value value) throws InterpreterError;
    Value getComponent(Value address) throws InterpreterError;
}
