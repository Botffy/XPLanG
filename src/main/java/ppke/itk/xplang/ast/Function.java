package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.Signature;

import java.util.List;

public class Function extends FunctionDeclaration {
    public Function(Location location, Signature signature, List<VariableDeclaration> parameters, Block block) {
        super(location, signature);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
