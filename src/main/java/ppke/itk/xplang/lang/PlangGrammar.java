package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.*;
import ppke.itk.xplang.function.Instruction;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.parser.operator.*;
import ppke.itk.xplang.type.Archetype;
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
            makeSymbol(PlangSymbol.LOOP, props).register(ctx);
            makeSymbol(PlangSymbol.WHILE, props).register(ctx);
            makeSymbol(PlangSymbol.END_LOOP, props).register(ctx);
            makeSymbol(PlangSymbol.ASSIGNMENT, props).register(ctx);
            makeSymbol(PlangSymbol.COLON, props).register(ctx);
            makeSymbol(PlangSymbol.COMMA, props).register(ctx);
            Symbol parenOpen = makeSymbol(PlangSymbol.PAREN_OPEN, props).register(ctx);
            Symbol parenClose = makeSymbol(PlangSymbol.PAREN_CLOSE, props).register(ctx);
            Symbol bracketOpen = makeSymbol(PlangSymbol.BRACKET_OPEN, props).register(ctx);
            Symbol bracketClose = makeSymbol(PlangSymbol.BRACKET_CLOSE, props).register(ctx);
            Symbol not = makeSymbol(PlangSymbol.OPERATOR_NOT, props).register(ctx);
            Symbol or = makeSymbol(PlangSymbol.OPERATOR_OR, props).register(ctx);
            Symbol and = makeSymbol(PlangSymbol.OPERATOR_AND, props).register(ctx);
            Symbol minus = makeSymbol(PlangSymbol.OPERATOR_MINUS, props).register(ctx);
            Symbol plus = makeSymbol(PlangSymbol.OPERATOR_PLUS, props).register(ctx);
            Symbol times = makeSymbol(PlangSymbol.OPERATOR_TIMES, props).register(ctx);
            Symbol idiv = makeSymbol(PlangSymbol.OPERATOR_IDIV, props).register(ctx);
            Symbol div = makeSymbol(PlangSymbol.OPERATOR_DIV, props).register(ctx);
            Symbol mod = makeSymbol(PlangSymbol.OPERATOR_IMOD, props).register(ctx);
            Symbol exp = makeSymbol(PlangSymbol.OPERATOR_EXP, props).register(ctx);
            Symbol pipe = makeSymbol(PlangSymbol.OPERATOR_PIPE, props).register(ctx);
            Symbol eq = makeSymbol(PlangSymbol.OPERATOR_EQ, props).register(ctx);
            Symbol neq = makeSymbol(PlangSymbol.OPERATOR_NEQ, props).register(ctx);
            Symbol lt = makeSymbol(PlangSymbol.OPERATOR_LT, props).register(ctx);
            Symbol lte = makeSymbol(PlangSymbol.OPERATOR_LTE, props).register(ctx);
            Symbol gt = makeSymbol(PlangSymbol.OPERATOR_GT, props).register(ctx);
            Symbol gte = makeSymbol(PlangSymbol.OPERATOR_GTE, props).register(ctx);
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

            createComparisons(ctx, Archetype.INTEGER_TYPE);
            createComparisons(ctx, Archetype.REAL_TYPE);
            createComparisons(ctx, Archetype.CHARACTER_TYPE);
            createComparisons(ctx, Archetype.STRING_TYPE);
            ctx.createBuiltin(name("builtin$not"), Instruction.NOT);
            ctx.createBuiltin(name("builtin$or"), Instruction.OR);
            ctx.createBuiltin(name("builtin$and"), Instruction.AND);
            ctx.createBuiltin(name("builtin$negate"), Instruction.INEG);
            ctx.createBuiltin(name("builtin$length"), Instruction.IABS);
            ctx.createBuiltin(name("builtin$minus"), Instruction.ISUB);
            ctx.createBuiltin(name("builtin$plus"), Instruction.ISUM);
            ctx.createBuiltin(name("builtin$times"), Instruction.IMUL);
            ctx.createBuiltin(name("builtin$div"), Instruction.IDIV);
            ctx.createBuiltin(name("builtin$mod"), Instruction.IMOD);
            ctx.createBuiltin(name("builtin$rand"), Instruction.RAND);
            ctx.createBuiltin(name("builtin$negate"), Instruction.FNEG);
            ctx.createBuiltin(name("builtin$length"), Instruction.FABS);
            ctx.createBuiltin(name("builtin$minus"), Instruction.FSUB);
            ctx.createBuiltin(name("builtin$plus"), Instruction.FSUM);
            ctx.createBuiltin(name("builtin$times"), Instruction.FMUL);
            ctx.createBuiltin(name("builtin$div"), Instruction.FDIV);
            ctx.createBuiltin(name("builtin$exp"), Instruction.FEXP);

            ctx.createBuiltin(name("builtin$length"), Instruction.ARLEN);

            ctx.createBuiltin(name(props.getFunctionName("rand")), Instruction.RAND);
            ctx.createBuiltin(name(props.getFunctionName("sin")), Instruction.SIN);
            ctx.createBuiltin(name(props.getFunctionName("cos")), Instruction.COS);
            ctx.createBuiltin(name(props.getFunctionName("tan")), Instruction.TAN);
            ctx.createBuiltin(name(props.getFunctionName("asin")), Instruction.ASIN);
            ctx.createBuiltin(name(props.getFunctionName("acos")), Instruction.ACOS);
            ctx.createBuiltin(name(props.getFunctionName("atan")), Instruction.ATAN);
            ctx.createBuiltin(name(props.getFunctionName("ld")), Instruction.LD);
            ctx.createBuiltin(name(props.getFunctionName("exp")), Instruction.EXP);

            ctx.createBuiltin(name(props.getFunctionName("nagy")), Instruction.NAGY);
            ctx.createBuiltin(name(props.getFunctionName("kis")), Instruction.KIS);
            ctx.createBuiltin(name(props.getFunctionName("is_letter")), Instruction.IS_LETTER);
            ctx.createBuiltin(name(props.getFunctionName("is_digit")), Instruction.IS_DIGIT);

            ctx.prefix(parenOpen, new Grouping(parenClose));
            ctx.prefix(identifier, new IdentifierOperator(PlangGrammar::name));
            ctx.prefix(literalInt, new LiteralOperator<>(IntegerLiteral::new, Integer::valueOf));
            ctx.prefix(literalReal, new LiteralOperator<>(RealLiteral::new, Double::valueOf));
            ctx.prefix(literalBool, new LiteralOperator<>(BooleanLiteral::new, x -> x.equalsIgnoreCase(props.get("value.boolean.true"))));
            ctx.prefix(literalChar, new LiteralOperator<>(CharacterLiteral::new, x -> x.charAt(1)));
            ctx.prefix(literalText, new LiteralOperator<>(StringLiteral::new, x -> x.substring(1, x.length() - 1)));
            ctx.infix(bracketOpen, new ElementValueOperator(bracketClose));

            ctx.infix(eq, new InfixBinary(name("builtin$eq"), Operator.Precedence.RELATIONAL));
            ctx.infix(neq, new InfixBinary(name("builtin$neq"), Operator.Precedence.RELATIONAL));
            ctx.infix(lt, new InfixBinary(name("builtin$lt"), Operator.Precedence.RELATIONAL));
            ctx.infix(lte, new InfixBinary(name("builtin$lte"), Operator.Precedence.RELATIONAL));
            ctx.infix(gt, new InfixBinary(name("builtin$gt"), Operator.Precedence.RELATIONAL));
            ctx.infix(gte, new InfixBinary(name("builtin$gte"), Operator.Precedence.RELATIONAL));

            ctx.prefix(not, new PrefixUnary(name("builtin$not")));
            ctx.infix(or, new InfixBinary(name("builtin$or"), Operator.Precedence.LOGIC));
            ctx.infix(and, new InfixBinary(name("builtin$and"), Operator.Precedence.LOGIC));

            ctx.prefix(minus, new PrefixUnary(name("builtin$negate")));
            ctx.prefix(pipe, new CircumfixOperator(pipe, name("builtin$length")));
            ctx.infix(minus, new InfixBinary(name("builtin$minus"), Operator.Precedence.SUM));
            ctx.infix(plus, new InfixBinary(name("builtin$plus"), Operator.Precedence.SUM));
            ctx.infix(plus, new InfixBinary(name("builtin$plus"), Operator.Precedence.SUM));
            ctx.infix(times, new InfixBinary(name("builtin$times"), Operator.Precedence.PRODUCT));
            ctx.infix(idiv, new InfixBinary(name("builtin$div"), Operator.Precedence.PRODUCT));
            ctx.infix(div, new InfixBinary(name("builtin$div"), Operator.Precedence.PRODUCT));
            ctx.infix(mod, new InfixBinary(name("builtin$mod"), Operator.Precedence.PRODUCT));
            ctx.infix(exp, new InfixBinary(name("builtin$exp"), Operator.Precedence.EXPONENT));
        } catch(ParseError | IllegalStateException error) {
            throw new IllegalStateException("Failed to initialise PlangGrammar", error);
        }
    }

    private void createComparisons(Context ctx, Type type) throws NameClashError {
        ctx.createBuiltin(name("builtin$eq"), Instruction.EQ, Archetype.BOOLEAN_TYPE, type, type);
        ctx.createBuiltin(name("builtin$neq"), Instruction.NEQ, Archetype.BOOLEAN_TYPE, type, type);
        ctx.createBuiltin(name("builtin$lt"), Instruction.LT, Archetype.BOOLEAN_TYPE, type, type);
        ctx.createBuiltin(name("builtin$lte"), Instruction.LTE, Archetype.BOOLEAN_TYPE, type, type);
        ctx.createBuiltin(name("builtin$gt"), Instruction.GT, Archetype.BOOLEAN_TYPE, type, type);
        ctx.createBuiltin(name("builtin$gte"), Instruction.GTE, Archetype.BOOLEAN_TYPE, type, type);
        ctx.createBuiltin(name("builtin$"), Instruction.GTE, Archetype.BOOLEAN_TYPE, type, type);
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
