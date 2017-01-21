package ppke.itk.xplang.parser.operator;

import ppke.itk.xplang.ast.FunctionCall;
import ppke.itk.xplang.ast.FunctionDeclaration;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.common.Location;

import java.util.List;
import java.util.Set;

public class Function extends Expression {
    private final Set<FunctionDeclaration> functions;
    private final List<Expression> childNodes;

    protected Function(Location location, Set<FunctionDeclaration> functions, List<Expression> childNodes) {
        super(location);
        this.functions = functions;
        this.childNodes = childNodes;
    }

    @Override public List<Expression> childNodes() {
        return childNodes;
    }

    @Override protected RValue toASTNode() {
        return new FunctionCall(location(), functions.iterator().next());
    }
}
