package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.Type;

public class ElementVal extends RValue {
    private final Type type;

    /**
     * Constructor
     * @param addressable An addressable value: an expression resolving to a composite type.
     * @param address A value that addresses the addressable.
     * @param type The statically calculated type of the value.
     */
    public ElementVal(Location location, RValue addressable, RValue address, Type type) {
        super(location);
        this.type = type;
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

    @Override
    public Type getType() {
        return type;
    }

    @Override public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
