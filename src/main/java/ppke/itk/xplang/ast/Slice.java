package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.Type;

/**
 * Get a slice of an Addressable value.
 */
public class Slice extends RValue {
    /**
     * Constructor.
     * @param location the location of the expression in the source code
     * @param slicable a slicable value: an expression resolving to a composite type that can be sliced.
     * @param startIndex the starting index of the slice (inclusive).
     * @param endIndex the ending index of the slice (exclusive).
     */
    public Slice(Location location, RValue slicable, RValue startIndex, RValue endIndex) {
        super(location);
        this.children.add(0, slicable);
        this.children.add(1, startIndex);
        this.children.add(2, endIndex);
    }

    public RValue getSlicable() {
        return (RValue) this.children.get(0);
    }

    public RValue getStartIndex() {
        return (RValue) this.children.get(1);
    }

    public RValue getEndIndex() {
        return (RValue) this.children.get(2);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Type getType() {
        return getSlicable().getType();
    }
}
