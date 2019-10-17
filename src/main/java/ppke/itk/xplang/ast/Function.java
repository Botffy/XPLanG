package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.Signature;

import java.util.List;

public class Function extends FunctionDeclaration {
    private final List<VariableDeclaration> parameters;

    public Function(Location location, Signature signature, List<VariableDeclaration> parameters, Block block) {
        super(location, signature);
        this.parameters = parameters;
        children.add(0, block);
    }

    public Block block() {
        return (Block) children.get(0);
    }

    public void setBlock(Block block) {
        this.children.set(0, block);
    }

    public List<VariableDeclaration> parameters() {
        return parameters;
    }

    @Override
    public boolean isDefined() {
        return this.children.get(0) != null;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
