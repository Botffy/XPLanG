package ppke.itk.xplang.parser.operator;

import ppke.itk.xplang.ast.RValue;

import java.util.List;

/**
 * A mutable parse tree produced by the {@link ExpressionParser}.
 */
public abstract class Expression {
    abstract protected List<Expression> childNodes();
    abstract public RValue toASTNode();
}
