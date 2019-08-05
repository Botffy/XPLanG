package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.*;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.function.Instruction;
import ppke.itk.xplang.parser.operator.CircumfixOperator;
import ppke.itk.xplang.parser.operator.LiteralOperator;
import ppke.itk.xplang.parser.operator.PrefixUnary;
import ppke.itk.xplang.parser.operator.IdentifierOperator;
import ppke.itk.xplang.type.Archetype;
import ppke.itk.xplang.type.FixArray;
import ppke.itk.xplang.type.Type;

public class PlangGrammar extends Grammar {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");

    @Override
    public void setup(Context ctx) {
        log.debug("Setting up context");
        try {
            LexicalProperties props = new LexicalProperties();

            makeSymbol(PlangSymbol.PROGRAM, props).register(ctx);
            makeSymbol(PlangSymbol.END_PROGRAM, props).register(ctx);
            makeSymbol(PlangSymbol.DECLARE, props).register(ctx);
            makeSymbol(PlangSymbol.IF, props).register(ctx);
            makeSymbol(PlangSymbol.THEN, props).register(ctx);
            makeSymbol(PlangSymbol.ELSE, props).register(ctx);
            makeSymbol(PlangSymbol.ENDIF, props).register(ctx);
            makeSymbol(PlangSymbol.ASSIGNMENT, props).register(ctx);
            makeSymbol(PlangSymbol.COLON, props).register(ctx);
            makeSymbol(PlangSymbol.COMMA, props).register(ctx);
            Symbol bracketOpen = makeSymbol(PlangSymbol.BRACKET_OPEN, props).register(ctx);
            Symbol bracketClose = makeSymbol(PlangSymbol.BRACKET_CLOSE, props).register(ctx);
            Symbol minus = makeSymbol(PlangSymbol.OPERATOR_MINUS, props).register(ctx);
            Symbol pipe = makeSymbol(PlangSymbol.OPERATOR_PIPE, props).register(ctx);
            Symbol identifier = makeSymbol(PlangSymbol.IDENTIFIER, props).withPrecedence(Symbol.Precedence.IDENTIFIER).register(ctx);
            Symbol literalInt = makeSymbol(PlangSymbol.LITERAL_INT, props).withPrecedence(Symbol.Precedence.LITERAL).register(ctx);
            Symbol literalReal = makeSymbol(PlangSymbol.LITERAL_REAL, props).withPrecedence(Symbol.Precedence.LITERAL).register(ctx);
            Symbol literalBool = makeSymbol(PlangSymbol.LITERAL_BOOL, props).withPrecedence(Symbol.Precedence.LITERAL).register(ctx);
            Symbol literalChar = makeSymbol(PlangSymbol.LITERAL_CHAR, props).withPrecedence(Symbol.Precedence.LITERAL).register(ctx);
            Symbol literalText = makeSymbol(PlangSymbol.LITERAL_STRING, props).withPrecedence(Symbol.Precedence.LITERAL).register(ctx);
            makeSymbol(PlangSymbol.EOL, props).notSignificant().register(ctx);
            makeSymbol(PlangSymbol.WHITESPACE, props).notSignificant().register(ctx);
            makeSymbol(PlangSymbol.COMMENT, props).notSignificant().register(ctx);

            makeType(ctx, Archetype.BOOLEAN_TYPE, props);
            makeType(ctx, Archetype.INTEGER_TYPE, props);
            makeType(ctx, Archetype.REAL_TYPE, props);
            makeType(ctx, Archetype.CHARACTER_TYPE, props);
            makeType(ctx, Archetype.STRING_TYPE, props);

            ctx.createBuiltin(name("builtin$negate"), Instruction.INEG, Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE);
            ctx.createBuiltin(name("builtin$length"), Instruction.ARLEN, Archetype.INTEGER_TYPE, FixArray.ANY_ARRAY);
            ctx.createBuiltin(name("builtin$length"), Instruction.ARLEN, Archetype.INTEGER_TYPE, Archetype.STRING_TYPE);

            ctx.prefix(identifier, new IdentifierOperator(PlangGrammar::name));
            ctx.prefix(literalInt, new LiteralOperator<>(IntegerLiteral::new, Integer::valueOf));
            ctx.prefix(literalReal, new LiteralOperator<>(RealLiteral::new, Double::valueOf));
            ctx.prefix(literalBool, new LiteralOperator<>(BooleanLiteral::new, x -> x.equalsIgnoreCase(props.get("value.boolean.true"))));
            ctx.prefix(literalChar, new LiteralOperator<>(CharacterLiteral::new, x -> x.charAt(1)));
            ctx.prefix(literalText, new LiteralOperator<>(StringLiteral::new, x -> x.substring(1, x.length() - 1)));
            ctx.prefix(minus, new PrefixUnary(name("builtin$negate")));
            ctx.prefix(pipe, new CircumfixOperator(pipe, name("builtin$length")));
            ctx.infix(bracketOpen, new ElementValueOperator(bracketClose));
        } catch(ParseError | IllegalStateException error) {
            throw new IllegalStateException("Failed to initialise PlangGrammar", error);
        }
    }

    /**
     * {@code start = Program}
     */
    @Override protected Root start(Parser parser) throws ParseError {
        log.debug("start");
        Program program = ProgramParser.parse(parser);
        return new Root(program.location(), program);
    }

    static PlangName name(String name) {
        return new PlangName(name);
    }

    private Symbol.Builder makeSymbol(PlangSymbol symbol, LexicalProperties props) {
        return Symbol.create()
            .named(symbol.name())
            .matching(props.getSymbolPattern(symbol))
            .caseInsensitive();
    }

    private void makeType(Context ctx, Type type, LexicalProperties props) throws ParseError {
        ctx.declareType(name(props.getTypeName(type)), type);
    }
}
