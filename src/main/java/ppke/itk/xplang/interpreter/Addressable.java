package ppke.itk.xplang.interpreter;

/**
 * Something that stores Values addressed by things. Like an array (stores whatever types, addressed by
 * IntegerValues. Or the memory (addressed by anything).
 */
interface Addressable {
    ReferenceValue getReference(Object address) throws InterpreterError;
    void setComponent(Object address, Value value) throws InterpreterError;
    Value getComponent(Object address) throws InterpreterError;
}
