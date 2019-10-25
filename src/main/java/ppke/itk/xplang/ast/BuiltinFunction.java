package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.function.Instruction;
import ppke.itk.xplang.type.Signature;

public class BuiltinFunction extends FunctionDeclaration {
    private final Instruction instruction;

    public BuiltinFunction(Location location, Signature signature, Instruction instruction) {
        super(location, signature);
        this.instruction = instruction;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    @Override
    public boolean isDefined() {
        return true;
    }

    @Override
    public boolean isPure() {
        return instruction.isPure();
    }

    @Override public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
