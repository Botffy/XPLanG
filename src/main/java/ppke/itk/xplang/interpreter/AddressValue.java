package ppke.itk.xplang.interpreter;

class AddressValue extends Value {
    private final Object address;

    AddressValue(Object address) {
        this.address = address;
    }

    Object getAddress() {
        return address;
    }

    @Override public String toString() {
        return String.format("Memory address %s", address);
    }

    @Override public int hashCode() {
        return address.hashCode();
    }

    @Override public boolean equals(Object object) {
        return object instanceof AddressValue && ((AddressValue) object).address == this.address;
    }
}
