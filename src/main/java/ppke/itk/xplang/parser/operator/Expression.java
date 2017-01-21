package ppke.itk.xplang.parser.operator;

import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.common.Location;

import java.util.List;

import static java.util.Collections.emptyList;

/**
 * A mutable parse tree produced by the {@link ExpressionParser}.
 */
public abstract class Expression {
    private final Location location;

    Expression(Location location) {
        this.location = location;
    }

    List<Expression> childNodes() {
        return emptyList();
    }

    protected final Location location() {
        return location;
    }

    abstract protected RValue toASTNode();
}
