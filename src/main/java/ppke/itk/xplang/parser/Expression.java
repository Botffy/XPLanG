package ppke.itk.xplang.parser;

import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.parser.operator.ExpressionParser;

import java.util.List;

/**
 * A mutable parse tree produced by the {@link ExpressionParser}.
 */
public abstract class Expression {
    abstract public List<Expression> childNodes();
    abstract RValue toASTNode();
}
