package ppke.itk.xplang.interpreter;

class MemoryAddress extends ReferenceValue {
    private final Memory memory;
    private final Object address;

    MemoryAddress(Memory memory, Object address) {
        this.memory = memory;
        this.address = address;
    }

    Object getAddress() {
        return address;
    }

    @Override void assign(Value value) {
        memory.set(this, value);
    }

    @Override public String toString() {
        return String.format("[Memory address %s]", address);
    }

    @Override public int hashCode() {
        return address.hashCode();
    }

    @Override public boolean equals(Object object) {
        return object instanceof MemoryAddress && ((MemoryAddress) object).address == this.address;
    }
}
