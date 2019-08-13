package ppke.itk.xplang.lang;

import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.common.Translator;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.type.Archetype;

class ConditionParser {
    private final static Translator translator = Translator.getInstance("Plang");

    private ConditionParser() { }

    static RValue parse(Parser parser) throws ParseError {
        Expression conditionExpression = parser.parseExpression();
        return TypeChecker.in(parser.context())
            .checking(conditionExpression)
            .expecting(Archetype.BOOLEAN_TYPE)
            .withCustomErrorMessage(
                node -> new TypeError(translator.translate("plang.conditional_must_be_boolean"), node.location())
            )
            .build()
            .resolve();
    }
}
