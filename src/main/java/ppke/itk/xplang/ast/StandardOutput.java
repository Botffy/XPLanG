package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.Archetype;
import ppke.itk.xplang.type.Type;

public class StandardOutput extends RValue {
    public StandardOutput() {
        super(Location.NONE);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Type getType() {
        return Archetype.OUTSTREAM_TYPE;
    }

    @Override
    public boolean isStatic() {
        return false;
    }
}
