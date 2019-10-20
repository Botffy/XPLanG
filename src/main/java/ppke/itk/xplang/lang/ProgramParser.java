package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.Block;
import ppke.itk.xplang.ast.Program;
import ppke.itk.xplang.ast.Scope;
import ppke.itk.xplang.ast.Sequence;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.*;

/**
 * {@code Program = PROGRAM IDENTIFIER [Declarations] Sequence END_PROGRAM}
 */
final class ProgramParser {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");

    private ProgramParser() { /* empty private ctor */ }

    static Program parse(Parser parser) throws ParseError {
        log.debug("Program");

        Token startToken = parser.accept(Symbol.PROGRAM, ErrorCode.EXPECTED_PROGRAM);
        Token nameToken = parser.accept(Symbol.IDENTIFIER, ErrorCode.EXPECTED_PROGRAM_NAME);

        if(parser.actual().symbol().equals(Symbol.DECLARE)) {
            DeclarationsParser.parse(parser).forEach(variable -> {
                try {
                    parser.context().declareVariable(new PlangName(variable.getName()), variable);
                } catch (ParseError error) {
                    parser.recordError(error.toErrorMessage());
                }
            });
        }

        Sequence sequence = SequenceParser.parse(parser, Symbol.END_PROGRAM);

        Token endToken = parser.accept(Symbol.END_PROGRAM, ErrorCode.EXPECTED_END_PROGRAM);
        Scope scope = parser.context().closeScope();

        return new Program(
            Location.between(startToken.location(), endToken.location()),
            nameToken.lexeme(),
            new Block(scope, sequence)
        );
    }
}
