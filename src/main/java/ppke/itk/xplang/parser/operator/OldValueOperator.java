package ppke.itk.xplang.parser.operator;

import ppke.itk.xplang.ast.OldVariableValue;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.ast.VariableDeclaration;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.*;

import java.util.function.Function;

public class OldValueOperator implements Operator.Prefix {
    private final Function<String, Name> nameCreator;

    public OldValueOperator(Function<String, Name> nameCreator) {
        this.nameCreator = nameCreator;
    }

    @Override
    public Expression parsePrefix(ExpressionParser parser) throws ParseError {
        Token op = parser.actual();
        Token var = parser.accept(Symbol.IDENTIFIER);
        Name name = nameCreator.apply(var.lexeme());

        if (!parser.context().isVariable(name)) {
            throw new ParseError(var.location(), ErrorCode.OPERATOR_OLD_EXPEECTS_VARIABLE);
        }

        VariableDeclaration variableDeclaration = parser.context().lookupVariable(name, var);
        return new ValueExpression(new OldVariableValue(
            Location.between(op.location(), var.location()),
            variableDeclaration
        ));
    }

    @Override
    public int getPrecedence() {
        return Precedence.GROUPING;
    }
}
