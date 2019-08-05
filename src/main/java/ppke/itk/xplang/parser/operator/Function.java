package ppke.itk.xplang.parser.operator;

import ppke.itk.xplang.ast.FunctionCall;
import ppke.itk.xplang.ast.FunctionDeclaration;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.common.Location;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class Function extends Expression {
    private final Location location;
    private final Set<FunctionDeclaration> functions;
    private final List<Expression> childNodes;

    protected Function(Location location, Set<FunctionDeclaration> functions, List<Expression> childNodes) {
        this.location = location;
        this.functions = functions;
        this.childNodes = childNodes;
    }

    @Override public List<Expression> childNodes() {
        return childNodes;
    }

    @Override public RValue toASTNode() {
        return new FunctionCall(location, functions.iterator().next(), childNodes.stream().map(Expression::toASTNode).collect(toList()));
    }
}
