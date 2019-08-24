package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.Archetype;
import ppke.itk.xplang.type.Type;

public class StandardInput extends RValue {
    public StandardInput() {
        super(Location.NONE);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Type getType() {
        return Archetype.INSTREAM_TYPE;
    }
}
