package ppke.itk.xplang.lang;

import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.ast.Assertion;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.type.Archetype;

class AssertStatementParser {
    static Assertion parse(Parser parser) throws ParseError {
        Location startLoc = parser.accept(parser.symbol(PlangSymbol.ASSERT)).location();

        Expression assertion = parser.parseExpression();
        Location endLoc = assertion.getLocation();
        RValue message = TypeChecker.in(parser.context())
            .checking(assertion)
            .expecting(Archetype.BOOLEAN_TYPE)
            .withCustomErrorMessage(
                node -> new ParseError(node.location(), ErrorCode.TYPE_MISMATCH_ASSERTION, node.getType())
            )
            .build()
            .resolve();

        Location location = Location.between(startLoc, endLoc);

        return new Assertion(location, message);
    }
}
