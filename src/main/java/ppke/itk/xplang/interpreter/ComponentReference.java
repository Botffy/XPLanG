package ppke.itk.xplang.interpreter;

import java.util.Objects;

public class ComponentReference implements ReferenceValue {
    private final Addressable composite;
    private final Value address;

    ComponentReference(Addressable composite, Value address) {
        this.composite = composite;
        this.address = address;
    }

    @Override public void assign(Value value) {
        this.composite.setComponent(this.address, value);
    }

    @Override public ComponentReference copy() {
        return this;
    }

    @Override public String toString() {
        return String.format("[Component reference to %s:%s]", composite, address);
    }

    @Override public int hashCode() {
        return Objects.hash(composite, address);
    }

    @Override public boolean equals(Object object) {
        if(!(object instanceof ComponentReference)) return false;
        ComponentReference that = (ComponentReference) object;
        return this.composite.equals(that.composite) && this.address.equals(that.address);
    }
}
