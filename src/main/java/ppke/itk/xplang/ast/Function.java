package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.Signature;

import java.util.List;
import java.util.Optional;

public class Function extends FunctionDeclaration {
    private final List<VariableDeclaration> parameters;

    public Function(Location location, Signature signature, List<VariableDeclaration> parameters) {
        super(location, signature);
        this.parameters = parameters;
        children.add(0, null);
        children.add(1, null); // precondition
        children.add(2, null); // postcondition
    }

    public Block block() {
        return (Block) children.get(0);
    }

    public void setBlock(Block block) {
        this.children.set(0, block);
    }

    public Optional<Assertion> precondition() {
        return Optional.ofNullable((Assertion) children.get(1));
    }

    public void setPrecondition(Assertion precondition) {
        children.set(1, precondition);
    }

    public Optional<Assertion> postcondition() {
        return Optional.ofNullable((Assertion) children.get(2));
    }

    public void setPostcondition(Assertion postcondition) {
        children.set(2, postcondition);
    }

    public List<VariableDeclaration> parameters() {
        return parameters;
    }

    @Override
    public boolean isDefined() {
        return this.children.get(0) != null;
    }

    @Override
    public boolean isPure() {
        return isPure(block().sequence());
    }

    private boolean isPure(Node parent) {
        if (parent instanceof FunctionCall) {
            return ((FunctionCall) parent).getDeclaration().isPure();
        }

        if (parent instanceof Input || parent instanceof Output) {
            return false;
        }

        for (Node child : parent.getChildren()) {
            if (!isPure(child)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
