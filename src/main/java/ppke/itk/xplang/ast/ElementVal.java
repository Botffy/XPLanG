package ppke.itk.xplang.ast;

import ppke.itk.xplang.type.Type;

public class ElementVal extends RValue {
    /**
     * Constructor
     * @param addressable An addressable value: an expression resolving to a composite type.
     * @param address A value that addresses the addressable.
     */
    public ElementVal(RValue addressable, RValue address) {
        this.children.add(0, addressable);
        this.children.add(1, address);
    }

    /**
     * Reference to an addressable value.
     */
    public RValue getAddressable() {
        return (RValue) this.children.get(0);
    }

    public RValue getAddress() {
        return (RValue) this.children.get(1);
    }

    @Override public Type getType() {
        return getAddressable().getType().elementType();
    }

    @Override public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
