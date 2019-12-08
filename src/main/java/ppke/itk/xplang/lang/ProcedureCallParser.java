package ppke.itk.xplang.lang;

import ppke.itk.xplang.ast.ExpressionStatement;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.ast.Statement;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static ppke.itk.xplang.common.Location.between;
import static ppke.itk.xplang.lang.PlangName.name;

public class ProcedureCallParser {
    public static Statement parse(Parser parser) throws ParseError {
        Token identifier = parser.accept(Symbol.IDENTIFIER);
        parser.accept(Symbol.PAREN_OPEN);

        List<Expression> arguments;
        if (parser.actual().symbol() == Symbol.PAREN_CLOSE) {
            arguments = emptyList();
        } else {
            arguments = new ArrayList<>();
            arguments.add(parser.parseExpression());
            while (parser.actual().symbol() == Symbol.COMMA) {
                parser.advance();
                arguments.add(parser.parseExpression());
            }
        }

        Token closingToken = parser.accept(Symbol.PAREN_CLOSE);

        Name functionName = name(identifier.lexeme());
        Location location = between(identifier.location(), closingToken.location());
        FunctionExpression call = new FunctionExpression(
            functionName,
            location,
            parser.context().findFunctionsFor(functionName),
            arguments
        );

        RValue resolved = TypeChecker.in(parser.context()).checking(call).build().resolve();
        return new ExpressionStatement(location, resolved);
    }
}
