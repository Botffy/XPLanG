package ppke.itk.xplang.lang;

import ppke.itk.xplang.ast.Assignment;
import ppke.itk.xplang.ast.LValue;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.ast.Statement;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.common.Translator;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.type.Archetype;
import ppke.itk.xplang.type.Type;

import java.util.List;

/** {@code CloseStatement = CLOSE LValue */
public class CloseStatementParser {
    private final static Translator translator = Translator.getInstance("Plang");

    public static Statement parse(Parser parser) throws ParseError {
        // TODO jobb hibakezelés

        Token token = parser.accept(parser.symbol(PlangSymbol.CLOSE));
        Location startLocation = token.location();

        LValue ref = LValueParser.parse(parser);
        RValue var = ref.toRValue();
        Location endLocation = var.location();
        Location location = Location.between(startLocation, endLocation);

        Type varType = var.getType();
        Name closeFunctionName = null;
        if (Archetype.INSTREAM_TYPE.accepts(varType)) {
            closeFunctionName = SpecialName.CLOSE_INPUTSTREAM;
        } else if (Archetype.OUTSTREAM_TYPE.accepts(varType)) {
            closeFunctionName = SpecialName.CLOSE_OUTPUTSTREAM;
        } else {
            throw new TypeError(
                translator.translate("plang.close_only_streams", Archetype.INSTREAM_TYPE, Archetype.OUTSTREAM_TYPE, varType),
                location
            );
        }

        Expression callExpression = new FunctionExpression(
            closeFunctionName,
            location,
            parser.context().findFunctionsFor(closeFunctionName),
            List.of(new ValueExpression(var))
        );

        RValue call = TypeChecker.in(parser.context()).checking(callExpression).build().resolve();

        return new Assignment(location, ref, call);
    }
}
