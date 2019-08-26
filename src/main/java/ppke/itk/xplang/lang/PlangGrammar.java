package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.*;
import ppke.itk.xplang.function.Instruction;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.parser.operator.*;
import ppke.itk.xplang.type.AddressableScalar;
import ppke.itk.xplang.type.Archetype;
import ppke.itk.xplang.type.Scalar;
import ppke.itk.xplang.type.Type;

import static ppke.itk.xplang.lang.PlangName.name;

/**
 * The description of the PLanG programming language (PLanG-strict)
 */
public class PlangGrammar extends Grammar {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");

    private final LexicalProperties props = new LexicalProperties();

    @Override
    public void setup(Context ctx) {
        log.debug("Setting up context");
        try {
            makeSymbol(PlangSymbol.PROGRAM).register(ctx);
            makeSymbol(PlangSymbol.END_PROGRAM).register(ctx);
            makeSymbol(PlangSymbol.DECLARE).register(ctx);
            makeSymbol(PlangSymbol.IF).register(ctx);
            makeSymbol(PlangSymbol.THEN).register(ctx);
            makeSymbol(PlangSymbol.ELSE).register(ctx);
            makeSymbol(PlangSymbol.ENDIF).register(ctx);
            makeSymbol(PlangSymbol.LOOP).register(ctx);
            makeSymbol(PlangSymbol.WHILE).register(ctx);
            makeSymbol(PlangSymbol.END_LOOP).register(ctx);
            makeSymbol(PlangSymbol.ASSIGNMENT).register(ctx);
            makeSymbol(PlangSymbol.IN).register(ctx);
            makeSymbol(PlangSymbol.OUT).register(ctx);
            makeSymbol(PlangSymbol.OPEN).register(ctx);
            makeSymbol(PlangSymbol.CLOSE).register(ctx);
            Symbol colon = makeSymbol(PlangSymbol.COLON).register(ctx);
            Symbol comma = makeSymbol(PlangSymbol.COMMA).register(ctx);
            Symbol parenOpen = makeSymbol(PlangSymbol.PAREN_OPEN).register(ctx);
            Symbol parenClose = makeSymbol(PlangSymbol.PAREN_CLOSE).register(ctx);
            Symbol bracketOpen = makeSymbol(PlangSymbol.BRACKET_OPEN).register(ctx);
            Symbol bracketClose = makeSymbol(PlangSymbol.BRACKET_CLOSE).register(ctx);
            Symbol not = makeSymbol(PlangSymbol.OPERATOR_NOT).register(ctx);
            Symbol or = makeSymbol(PlangSymbol.OPERATOR_OR).register(ctx);
            Symbol and = makeSymbol(PlangSymbol.OPERATOR_AND).register(ctx);
            Symbol minus = makeSymbol(PlangSymbol.OPERATOR_MINUS).register(ctx);
            Symbol plus = makeSymbol(PlangSymbol.OPERATOR_PLUS).register(ctx);
            Symbol times = makeSymbol(PlangSymbol.OPERATOR_TIMES).register(ctx);
            Symbol idiv = makeSymbol(PlangSymbol.OPERATOR_IDIV).register(ctx);
            Symbol div = makeSymbol(PlangSymbol.OPERATOR_DIV).register(ctx);
            Symbol mod = makeSymbol(PlangSymbol.OPERATOR_IMOD).register(ctx);
            Symbol exp = makeSymbol(PlangSymbol.OPERATOR_EXP).register(ctx);
            Symbol pipe = makeSymbol(PlangSymbol.OPERATOR_PIPE).register(ctx);
            Symbol eq = makeSymbol(PlangSymbol.OPERATOR_EQ).register(ctx);
            Symbol neq = makeSymbol(PlangSymbol.OPERATOR_NEQ).register(ctx);
            Symbol lt = makeSymbol(PlangSymbol.OPERATOR_LT).register(ctx);
            Symbol lte = makeSymbol(PlangSymbol.OPERATOR_LTE).register(ctx);
            Symbol gt = makeSymbol(PlangSymbol.OPERATOR_GT).register(ctx);
            Symbol gte = makeSymbol(PlangSymbol.OPERATOR_GTE).register(ctx);
            Symbol find = makeSymbol(PlangSymbol.OPERATOR_FIND).register(ctx);
            Symbol sv = makeSymbol(PlangSymbol.OPERATOR_SV).register(ctx);
            Symbol identifier = makeSymbol(PlangSymbol.IDENTIFIER).withPrecedence(Symbol.Precedence.IDENTIFIER).register(ctx);
            Symbol literalInt = makeSymbol(PlangSymbol.LITERAL_INT).withPrecedence(Symbol.Precedence.LITERAL).register(ctx);
            Symbol literalReal = makeSymbol(PlangSymbol.LITERAL_REAL).withPrecedence(Symbol.Precedence.LITERAL).register(ctx);
            Symbol literalBool = makeSymbol(PlangSymbol.LITERAL_BOOL).withPrecedence(Symbol.Precedence.LITERAL).register(ctx);
            Symbol literalChar = makeSymbol(PlangSymbol.LITERAL_CHAR).withPrecedence(Symbol.Precedence.LITERAL).register(ctx);
            Symbol literalText = makeSymbol(PlangSymbol.LITERAL_STRING).withPrecedence(Symbol.Precedence.LITERAL).register(ctx);
            makeSymbol(PlangSymbol.EOL).notSignificant().register(ctx);
            makeSymbol(PlangSymbol.WHITESPACE).notSignificant().register(ctx);
            makeSymbol(PlangSymbol.COMMENT).notSignificant().register(ctx);

            Scalar boolType = makeScalar(ctx, Archetype.BOOLEAN_TYPE);
            Scalar intType = makeScalar(ctx, Archetype.INTEGER_TYPE);
            Scalar realType = makeScalar(ctx, Archetype.REAL_TYPE);
            Scalar charType = makeScalar(ctx, Archetype.CHARACTER_TYPE);
            Scalar inputStreamType = makeScalar(ctx, Archetype.INSTREAM_TYPE);
            Scalar outputStreamType = makeScalar(ctx, Archetype.OUTSTREAM_TYPE);
            Scalar stringType = new AddressableScalar(
                props.getTypeName(Archetype.STRING_TYPE),
                charType,
                intType,
                Archetype.STRING_TYPE
            );
            ctx.declareType(name(stringType.getLabel()), stringType);
            ctx.setBooleanType(boolType);
            ctx.setIntegerType(intType);

            ctx.createBuiltin(SpecialName.TYPE_CONVERSION, Instruction.FTOI, intType, realType);
            ctx.createBuiltin(SpecialName.TYPE_CONVERSION, Instruction.ITOF, realType, intType);
            ctx.createBuiltin(SpecialName.IMPLICIT_COERCION, Instruction.ITOF, realType, intType);

            ctx.createBuiltin(SpecialName.READ_INPUT, Instruction.IREAD, intType);
            ctx.createBuiltin(SpecialName.READ_INPUT, Instruction.FREAD, realType);
            ctx.createBuiltin(SpecialName.READ_INPUT, Instruction.CREAD, charType);
            ctx.createBuiltin(SpecialName.READ_INPUT, Instruction.BREAD, boolType);
            ctx.createBuiltin(SpecialName.READ_INPUT, Instruction.SREAD, stringType);

            ctx.createBuiltin(SpecialName.OPEN_INPUT_FILE, Instruction.IFILE_OPEN, inputStreamType, stringType);
            ctx.createBuiltin(SpecialName.CLOSE_INPUTSTREAM, Instruction.IFILE_CLOSE, Archetype.NONE, inputStreamType);
            ctx.createBuiltin(SpecialName.OPEN_OUTPUT_FILE, Instruction.OFILE_OPEN, outputStreamType, stringType);
            ctx.createBuiltin(SpecialName.CLOSE_OUTPUTSTREAM, Instruction.OFILE_CLOSE, Archetype.NONE, outputStreamType);

            createComparisons(ctx, boolType, intType);
            createComparisons(ctx, boolType, realType);
            createComparisons(ctx, boolType, charType);
            createComparisons(ctx, boolType, stringType);
            ctx.createBuiltin(operator("not"), Instruction.NOT, boolType, boolType);
            ctx.createBuiltin(operator("or"), Instruction.OR, boolType, boolType, boolType);
            ctx.createBuiltin(operator("and"), Instruction.AND, boolType, boolType, boolType);
            ctx.createBuiltin(operator("negate"), Instruction.INEG, intType, intType);
            ctx.createBuiltin(operator("length"), Instruction.IABS, intType, intType);
            ctx.createBuiltin(operator("minus"), Instruction.ISUB, intType, intType, intType);
            ctx.createBuiltin(operator("plus"), Instruction.ISUM, intType, intType, intType);
            ctx.createBuiltin(operator("times"), Instruction.IMUL, intType, intType, intType);
            ctx.createBuiltin(operator("idiv"), Instruction.IDIV, intType, intType, intType);
            ctx.createBuiltin(operator("mod"), Instruction.IMOD, intType, intType, intType);
            ctx.createBuiltin(operator("pow"), Instruction.IEXP, intType, intType, intType);
            ctx.createBuiltin(operator("negate"), Instruction.FNEG, realType, realType);
            ctx.createBuiltin(operator("length"), Instruction.FABS, realType, realType);
            ctx.createBuiltin(operator("minus"), Instruction.FSUB, realType, realType, realType);
            ctx.createBuiltin(operator("plus"), Instruction.FSUM, realType, realType, realType);
            ctx.createBuiltin(operator("times"), Instruction.FMUL, realType, realType, realType);
            ctx.createBuiltin(operator("div"), Instruction.FDIV, realType, realType, realType);
            ctx.createBuiltin(operator("pow"), Instruction.FEXP, realType, realType, realType);
            ctx.createBuiltin(operator("plus"), Instruction.APPEND, stringType, stringType, charType);
            ctx.createBuiltin(operator("plus"), Instruction.PREPEND, stringType, charType, stringType);
            ctx.createBuiltin(operator("plus"), Instruction.CONCAT, stringType, stringType, stringType);
            ctx.createBuiltin(operator("find"), Instruction.FIND_CHAR, intType, stringType, charType);
            ctx.createBuiltin(operator("find"), Instruction.FIND_SUBSTR, intType, stringType, stringType);
            ctx.createBuiltin(operator("sv"), Instruction.NEWLINE, charType);

            ctx.createBuiltin(operator("length"), Instruction.ARLEN, intType, Archetype.ADDRESSABLE);

            ctx.createBuiltin(name(props.getFunctionName("rand")), Instruction.RAND, intType, intType);
            ctx.createBuiltin(name(props.getFunctionName("sin")), Instruction.SIN, realType, realType);
            ctx.createBuiltin(name(props.getFunctionName("cos")), Instruction.COS, realType, realType);
            ctx.createBuiltin(name(props.getFunctionName("tan")), Instruction.TAN, realType, realType);
            ctx.createBuiltin(name(props.getFunctionName("asin")), Instruction.ASIN, realType, realType);
            ctx.createBuiltin(name(props.getFunctionName("acos")), Instruction.ACOS, realType, realType);
            ctx.createBuiltin(name(props.getFunctionName("atan")), Instruction.ATAN, realType, realType);
            ctx.createBuiltin(name(props.getFunctionName("ld")), Instruction.LD, realType, realType);
            ctx.createBuiltin(name(props.getFunctionName("exp")), Instruction.EXP, realType, realType);
            ctx.createBuiltin(name(props.getFunctionName("round")), Instruction.ROUND, intType, realType);
            ctx.createBuiltin(name(props.getFunctionName("nagy")), Instruction.NAGY, charType, charType);
            ctx.createBuiltin(name(props.getFunctionName("kis")), Instruction.KIS, charType, charType);
            ctx.createBuiltin(name(props.getFunctionName("is_letter")), Instruction.IS_LETTER, boolType, charType);
            ctx.createBuiltin(name(props.getFunctionName("is_digit")), Instruction.IS_DIGIT, boolType, charType);
            ctx.createBuiltin(name(props.getFunctionName("end")), Instruction.IFILE_END, boolType, inputStreamType);

            ctx.prefix(parenOpen, new Grouping(parenClose));
            ctx.prefix(identifier, new IdentifierOperator(PlangName::new));
            ctx.prefix(literalInt, new LiteralOperator<>(IntegerLiteral::new, intType, Integer::valueOf));
            ctx.prefix(literalReal, new LiteralOperator<>(RealLiteral::new, realType, Double::valueOf));
            ctx.prefix(literalBool, new LiteralOperator<>(BooleanLiteral::new, boolType, x -> x.equalsIgnoreCase(props.get("value.boolean.true"))));
            ctx.prefix(literalChar, new LiteralOperator<>(CharacterLiteral::new, charType, x -> x.charAt(1)));
            ctx.prefix(literalText, new LiteralOperator<>(StringLiteral::new, stringType, x -> x.substring(1, x.length() - 1)));
            ctx.infix(bracketOpen, new ElementValueOperator(bracketClose, colon));

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
            ctx.infix(idiv, new InfixBinary(operator("idiv"), Operator.Precedence.PRODUCT));
            ctx.infix(div, new InfixBinary(operator("div"), Operator.Precedence.PRODUCT));
            ctx.infix(mod, new InfixBinary(operator("mod"), Operator.Precedence.PRODUCT));
            ctx.infix(exp, new InfixBinary(operator("pow"), Operator.Precedence.EXPONENT));

            ctx.infix(find, new InfixBinary(operator("find"), Operator.Precedence.EXPONENT));
            ctx.prefix(sv, new NullaryOperator(operator("sv")));
        } catch(ParseError | IllegalStateException error) {
            throw new IllegalStateException("Failed to initialise PlangGrammar", error);
        }
    }

    private void createComparisons(Context ctx, Scalar booleanType, Type type) throws NameClashError {
        ctx.createBuiltin(operator("eq"), Instruction.EQ, booleanType, type, type);
        ctx.createBuiltin(operator("neq"), Instruction.NEQ, booleanType, type, type);
        ctx.createBuiltin(operator("lt"), Instruction.LT, booleanType, type, type);
        ctx.createBuiltin(operator("lte"), Instruction.LTE, booleanType, type, type);
        ctx.createBuiltin(operator("gt"), Instruction.GT, booleanType, type, type);
        ctx.createBuiltin(operator("gte"), Instruction.GTE, booleanType, type, type);
    }

    /**
     * {@code start = Program}
     */
    @Override protected Root start(Parser parser) throws ParseError {
        log.debug("start");
        Program program = ProgramParser.parse(parser);
        return new Root(program.location(), program);
    }

    private Symbol.Builder makeSymbol(PlangSymbol symbol) {
        return Symbol.create()
            .named(symbol.name())
            .matching(props.getSymbolPattern(symbol))
            .caseInsensitive();
    }

    private Scalar makeScalar(Context ctx, Type archetype) throws ParseError {
        String typeName = props.getTypeName(archetype);
        Scalar type = new Scalar(typeName, archetype, archetype.getInitialization());
        ctx.declareType(name(typeName), type);
        return type;
    }

    private OperatorName operator(String operatorName) {
        return new OperatorName(props.getFunctionName(operatorName));
    }
}
