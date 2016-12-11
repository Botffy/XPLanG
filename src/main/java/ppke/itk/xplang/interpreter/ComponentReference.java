package ppke.itk.xplang.interpreter;

import java.util.Objects;

public class ComponentReference extends ReferenceValue {
    private final Addressable composite;
    private final Object address;

    ComponentReference(Addressable composite, Object address) {
        this.composite = composite;
        this.address = address;
    }

    @Override void assign(Value value) {
        this.composite.setComponent(this.address, value);
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
