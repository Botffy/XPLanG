package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;

public class Output extends Statement {
    public Output(Location location, RValue output) {
        super(location);
        this.children.add(0, output);
    }

    public RValue getOutput() {
        return (RValue) this.children.get(0);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
