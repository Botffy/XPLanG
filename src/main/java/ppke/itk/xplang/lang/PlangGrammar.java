package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.*;
import ppke.itk.xplang.function.Instruction;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.parser.operator.*;
import ppke.itk.xplang.type.Archetype;
import ppke.itk.xplang.type.Type;

import static ppke.itk.xplang.lang.PlangName.name;
import static ppke.itk.xplang.parser.OperatorName.operator;

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
            Symbol colon = makeSymbol(PlangSymbol.COLON, props).register(ctx);
            Symbol comma = makeSymbol(PlangSymbol.COMMA, props).register(ctx);
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
            Symbol find = makeSymbol(PlangSymbol.OPERATOR_FIND, props).register(ctx);
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

            ctx.createBuiltin(SpecialName.TYPE_CONVERSION, Instruction.FTOI);
            ctx.createBuiltin(SpecialName.TYPE_CONVERSION, Instruction.ITOF);
            ctx.createBuiltin(SpecialName.IMPLICIT_COERCION, Instruction.ITOF);

            createComparisons(ctx, Archetype.INTEGER_TYPE);
            createComparisons(ctx, Archetype.REAL_TYPE);
            createComparisons(ctx, Archetype.CHARACTER_TYPE);
            createComparisons(ctx, Archetype.STRING_TYPE);
            ctx.createBuiltin(operator("not"), Instruction.NOT);
            ctx.createBuiltin(operator("or"), Instruction.OR);
            ctx.createBuiltin(operator("and"), Instruction.AND);
            ctx.createBuiltin(operator("negate"), Instruction.INEG);
            ctx.createBuiltin(operator("length"), Instruction.IABS);
            ctx.createBuiltin(operator("minus"), Instruction.ISUB);
            ctx.createBuiltin(operator("plus"), Instruction.ISUM);
            ctx.createBuiltin(operator("times"), Instruction.IMUL);
            ctx.createBuiltin(operator("div"), Instruction.IDIV);
            ctx.createBuiltin(operator("mod"), Instruction.IMOD);
            ctx.createBuiltin(operator("rand"), Instruction.RAND);
            ctx.createBuiltin(operator("negate"), Instruction.FNEG);
            ctx.createBuiltin(operator("length"), Instruction.FABS);
            ctx.createBuiltin(operator("minus"), Instruction.FSUB);
            ctx.createBuiltin(operator("plus"), Instruction.FSUM);
            ctx.createBuiltin(operator("times"), Instruction.FMUL);
            ctx.createBuiltin(operator("div"), Instruction.FDIV);
            ctx.createBuiltin(operator("exp"), Instruction.FEXP);
            ctx.createBuiltin(operator("plus"), Instruction.APPEND);
            ctx.createBuiltin(operator("plus"), Instruction.PREPEND);
            ctx.createBuiltin(operator("plus"), Instruction.CONCAT);
            ctx.createBuiltin(operator("find"), Instruction.FIND_CHAR);
            ctx.createBuiltin(operator("find"), Instruction.FIND_SUBSTR);

            ctx.createBuiltin(operator("slice"), Instruction.SLICE, Archetype.STRING_TYPE, Archetype.STRING_TYPE, Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE);
            ctx.createBuiltin(operator("length"), Instruction.ARLEN);

            ctx.createBuiltin(name(props.getFunctionName("rand")), Instruction.RAND);
            ctx.createBuiltin(name(props.getFunctionName("sin")), Instruction.SIN);
            ctx.createBuiltin(name(props.getFunctionName("cos")), Instruction.COS);
            ctx.createBuiltin(name(props.getFunctionName("tan")), Instruction.TAN);
            ctx.createBuiltin(name(props.getFunctionName("asin")), Instruction.ASIN);
            ctx.createBuiltin(name(props.getFunctionName("acos")), Instruction.ACOS);
            ctx.createBuiltin(name(props.getFunctionName("atan")), Instruction.ATAN);
            ctx.createBuiltin(name(props.getFunctionName("ld")), Instruction.LD);
            ctx.createBuiltin(name(props.getFunctionName("exp")), Instruction.EXP);
            ctx.createBuiltin(name(props.getFunctionName("round")), Instruction.ROUND);
            ctx.createBuiltin(name(props.getFunctionName("nagy")), Instruction.NAGY);
            ctx.createBuiltin(name(props.getFunctionName("kis")), Instruction.KIS);
            ctx.createBuiltin(name(props.getFunctionName("is_letter")), Instruction.IS_LETTER);
            ctx.createBuiltin(name(props.getFunctionName("is_digit")), Instruction.IS_DIGIT);

            ctx.prefix(parenOpen, new Grouping(parenClose));
            ctx.prefix(identifier, new IdentifierOperator(PlangName::new));
            ctx.prefix(literalInt, new LiteralOperator<>(IntegerLiteral::new, Integer::valueOf));
            ctx.prefix(literalReal, new LiteralOperator<>(RealLiteral::new, Double::valueOf));
            ctx.prefix(literalBool, new LiteralOperator<>(BooleanLiteral::new, x -> x.equalsIgnoreCase(props.get("value.boolean.true"))));
            ctx.prefix(literalChar, new LiteralOperator<>(CharacterLiteral::new, x -> x.charAt(1)));
            ctx.prefix(literalText, new LiteralOperator<>(StringLiteral::new, x -> x.substring(1, x.length() - 1)));
            ctx.infix(bracketOpen, new ElementValueOperator(bracketClose, colon, operator("slice")));

            ctx.infix(eq, new InfixBinary(operator("eq"), Operator.Precedence.RELATIONAL));
            ctx.infix(neq, new InfixBinary(operator("neq"), Operator.Precedence.RELATIONAL));
            ctx.infix(lt, new InfixBinary(operator("lt"), Operator.Precedence.RELATIONAL));
            ctx.infix(lte, new InfixBinary(operator("lte"), Operator.Precedence.RELATIONAL));
            ctx.infix(gt, new InfixBinary(operator("gt"), Operator.Precedence.RELATIONAL));
            ctx.infix(gte, new InfixBinary(operator("gte"), Operator.Precedence.RELATIONAL));

            ctx.prefix(not, new PrefixUnary(operator("not")));
            ctx.infix(or, new InfixBinary(operator("or"), Operator.Precedence.LOGIC));
            ctx.infix(and, new InfixBinary(operator("and"), Operator.Precedence.LOGIC));

            ctx.prefix(minus, new PrefixUnary(operator("negate")));
            ctx.prefix(pipe, new CircumfixOperator(pipe, operator("length")));
            ctx.infix(minus, new InfixBinary(operator("minus"), Operator.Precedence.SUM));
            ctx.infix(plus, new InfixBinary(operator("plus"), Operator.Precedence.SUM));
            ctx.infix(plus, new InfixBinary(operator("plus"), Operator.Precedence.SUM));
            ctx.infix(times, new InfixBinary(operator("times"), Operator.Precedence.PRODUCT));
            ctx.infix(idiv, new InfixBinary(operator("div"), Operator.Precedence.PRODUCT));
            ctx.infix(div, new InfixBinary(operator("div"), Operator.Precedence.PRODUCT));
            ctx.infix(mod, new InfixBinary(operator("mod"), Operator.Precedence.PRODUCT));
            ctx.infix(exp, new InfixBinary(operator("exp"), Operator.Precedence.EXPONENT));

            ctx.infix(find, new InfixBinary(operator("find"), Operator.Precedence.EXPONENT));
        } catch(ParseError | IllegalStateException error) {
            throw new IllegalStateException("Failed to initialise PlangGrammar", error);
        }
    }

    private void createComparisons(Context ctx, Type type) throws NameClashError {
        ctx.createBuiltin(operator("eq"), Instruction.EQ, Archetype.BOOLEAN_TYPE, type, type);
        ctx.createBuiltin(operator("neq"), Instruction.NEQ, Archetype.BOOLEAN_TYPE, type, type);
        ctx.createBuiltin(operator("lt"), Instruction.LT, Archetype.BOOLEAN_TYPE, type, type);
        ctx.createBuiltin(operator("lte"), Instruction.LTE, Archetype.BOOLEAN_TYPE, type, type);
        ctx.createBuiltin(operator("gt"), Instruction.GT, Archetype.BOOLEAN_TYPE, type, type);
        ctx.createBuiltin(operator("gte"), Instruction.GTE, Archetype.BOOLEAN_TYPE, type, type);
    }

    /**
     * {@code start = Program}
     */
    @Override protected Root start(Parser parser) throws ParseError {
        log.debug("start");
        Program program = ProgramParser.parse(parser);
        return new Root(program.location(), program);
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
