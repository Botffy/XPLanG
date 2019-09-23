package ppke.itk.xplang.interpreter;

import static ppke.itk.xplang.interpreter.ValueUtils.convert;

abstract class IntegerAddressable extends AddressableValue {
    @Override
    public final ReferenceValue getReference(Value address) throws InterpreterError {
        return new ComponentReference(this, address);
    }

    @Override
    public final void setComponent(Value address, Value value) throws InterpreterError {
        set(getIntegerIndex(address), value);
    }

    @Override
    public final Value getComponent(Value address) throws InterpreterError {
        return get(getIntegerIndex(address));
    }

    protected abstract void set(int index, Value value);
    protected abstract Value get(int index);

    private int getIntegerIndex(Value indexValue) {
        int index;
        try {
            index = convert(indexValue, IntegerValue.class).getValue();
        } catch (ClassCastException e) {
            throw new IllegalStateException(e);
        }

        if (index < 0 || index >= size()) {
            throw new InterpreterError(ErrorCode.ILLEGAL_INDEX);
        }

        return index;
    }
}
