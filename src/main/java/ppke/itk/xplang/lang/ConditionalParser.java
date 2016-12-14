package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.Conditional;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.ast.Sequence;
import ppke.itk.xplang.ast.Statement;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.common.Translator;
import ppke.itk.xplang.parser.ParseError;
import ppke.itk.xplang.parser.Parser;
import ppke.itk.xplang.parser.TypeError;
import ppke.itk.xplang.type.Scalar;

/**
 * {@code Conditional = IF RValue THEN Sequence ELSE Sequence ENDIF}
 */
final class ConditionalParser {
    private final static Translator translator = Translator.getInstance("Plang");
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");

    private ConditionalParser() { /* empty private ctor */ }

    static Statement parse(Parser parser) throws ParseError {
        log.debug("Conditional");
        Location startLoc = parser.accept(PlangSymbol.IF.symbol()).location();

        RValue condition = RValueParser.parse(parser);
        if(!Scalar.BOOLEAN_TYPE.accepts(condition.getType())) {
            throw new TypeError(translator.translate("plang.conditional_must_be_boolean"), condition.location());
        }
        parser.accept(PlangSymbol.THEN.symbol());
        Sequence ifBranch = SequenceParser.parse(parser, PlangSymbol.ENDIF.symbol(), PlangSymbol.ELSE.symbol());

        Sequence elseBranch = null;
        if(parser.actual().symbol().equals(PlangSymbol.ELSE.symbol())) {
            parser.advance();
            elseBranch = SequenceParser.parse(parser, PlangSymbol.ENDIF.symbol());
        }

        Location endLoc = parser.accept(PlangSymbol.ENDIF.symbol()).location();
        return new Conditional(new Location(startLoc.start, endLoc.end), condition, ifBranch, elseBranch);
    }
}
