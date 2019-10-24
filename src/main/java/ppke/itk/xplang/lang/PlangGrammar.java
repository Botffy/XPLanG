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

import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static ppke.itk.xplang.lang.TypeName.typeName;
import static ppke.itk.xplang.util.AccentUtils.calculateVariants;

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
            for (String variant : calculateVariants(stringType.getLabel())) {
                ctx.declareType(typeName(variant), stringType);
            }

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

            ctx.createBuiltin(operator("eq"), Instruction.EQ, boolType, boolType, boolType);
            ctx.createBuiltin(operator("neq"), Instruction.NEQ, boolType, boolType, boolType);
            createComparisons(ctx, boolType, intType);
            createComparisons(ctx, boolType, realType);
            createComparisons(ctx, boolType, charType);
            createComparisons(ctx, boolType, stringType);
            ctx.createBuiltin(operator("not"), Instruction.NOT, boolType, boolType);
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

            ctx.createBuiltin(aliases(props.getFunctionName("rand")), Instruction.RAND, intType, intType);
            ctx.createBuiltin(aliases(props.getFunctionName("sin")), Instruction.SIN, realType, realType);
            ctx.createBuiltin(aliases(props.getFunctionName("cos")), Instruction.COS, realType, realType);
            ctx.createBuiltin(aliases(props.getFunctionName("tan")), Instruction.TAN, realType, realType);
            ctx.createBuiltin(aliases(props.getFunctionName("asin")), Instruction.ASIN, realType, realType);
            ctx.createBuiltin(aliases(props.getFunctionName("acos")), Instruction.ACOS, realType, realType);
            ctx.createBuiltin(aliases(props.getFunctionName("atan")), Instruction.ATAN, realType, realType);
            ctx.createBuiltin(aliases(props.getFunctionName("ld")), Instruction.LD, realType, realType);
            ctx.createBuiltin(aliases(props.getFunctionName("exp")), Instruction.EXP, realType, realType);
            ctx.createBuiltin(aliases(props.getFunctionName("round")), Instruction.ROUND, intType, realType);
            ctx.createBuiltin(aliases(props.getFunctionName("nagy")), Instruction.NAGY, charType, charType);
            ctx.createBuiltin(aliases(props.getFunctionName("kis")), Instruction.KIS, charType, charType);
            ctx.createBuiltin(aliases(props.getFunctionName("is_letter")), Instruction.IS_LETTER, boolType, charType);
            ctx.createBuiltin(aliases(props.getFunctionName("is_digit")), Instruction.IS_DIGIT, boolType, charType);
            ctx.createBuiltin(aliases(props.getFunctionName("end")), Instruction.IFILE_END, boolType, inputStreamType);

            ctx.prefix(Symbol.PAREN_OPEN, new Grouping(Symbol.PAREN_CLOSE));
            ctx.prefix(Symbol.IDENTIFIER, new IdentifierOperator(PlangName::new, new IdentifierOperator.FunctionSymbols(
                Symbol.PAREN_OPEN, Symbol.COMMA, Symbol.PAREN_CLOSE
            )));
            ctx.prefix(Symbol.LITERAL_INT, new LiteralOperator<>(IntegerLiteral::new, intType, Integer::valueOf));
            ctx.prefix(Symbol.LITERAL_REAL, new LiteralOperator<>(RealLiteral::new, realType, Double::valueOf));
            ctx.prefix(Symbol.LITERAL_TRUE, new LiteralOperator<>(BooleanLiteral::new, boolType, x -> true) );
            ctx.prefix(Symbol.LITERAL_FALSE, new LiteralOperator<>(BooleanLiteral::new, boolType, x -> false) );
            ctx.prefix(Symbol.LITERAL_CHAR, new LiteralOperator<>(CharacterLiteral::new, charType, x -> x.charAt(1)));
            ctx.prefix(Symbol.LITERAL_STRING, new LiteralOperator<>(StringLiteral::new, stringType, x -> x.substring(1, x.length() - 1)));
            ctx.infix(Symbol.BRACKET_OPEN, new ElementValueOperator(Symbol.BRACKET_CLOSE, Symbol.COLON));

            ctx.infix(Symbol.OPERATOR_EQ, new InfixBinary(operator("eq"), Operator.Precedence.RELATIONAL));
            ctx.infix(Symbol.OPERATOR_NEQ, new InfixBinary(operator("neq"), Operator.Precedence.RELATIONAL));
            ctx.infix(Symbol.OPERATOR_LT, new InfixBinary(operator("lt"), Operator.Precedence.RELATIONAL));
            ctx.infix(Symbol.OPERATOR_LTE, new InfixBinary(operator("lte"), Operator.Precedence.RELATIONAL));
            ctx.infix(Symbol.OPERATOR_GT, new InfixBinary(operator("gt"), Operator.Precedence.RELATIONAL));
            ctx.infix(Symbol.OPERATOR_GTE, new InfixBinary(operator("gte"), Operator.Precedence.RELATIONAL));

            ctx.prefix(Symbol.OPERATOR_NOT, new PrefixUnary(operator("not")));
            ctx.infix(Symbol.OPERATOR_OR, new ConditionalConnectiveOperator(ConditionalConnective.Op.OR, boolType));
            ctx.infix(Symbol.OPERATOR_AND, new ConditionalConnectiveOperator(ConditionalConnective.Op.AND, boolType));

            ctx.prefix(Symbol.OPERATOR_MINUS, new PrefixUnary(operator("negate")));
            ctx.prefix(Symbol.OPERATOR_PIPE, new CircumfixOperator(Symbol.OPERATOR_PIPE, operator("length")));
            ctx.infix(Symbol.OPERATOR_MINUS, new InfixBinary(operator("minus"), Operator.Precedence.SUM));
            ctx.infix(Symbol.OPERATOR_PLUS, new InfixBinary(operator("plus"), Operator.Precedence.SUM));
            ctx.infix(Symbol.OPERATOR_PLUS, new InfixBinary(operator("plus"), Operator.Precedence.SUM));
            ctx.infix(Symbol.OPERATOR_TIMES, new InfixBinary(operator("times"), Operator.Precedence.PRODUCT));
            ctx.infix(Symbol.OPERATOR_IDIV, new InfixBinary(operator("idiv"), Operator.Precedence.PRODUCT));
            ctx.infix(Symbol.OPERATOR_DIV, new InfixBinary(operator("div"), Operator.Precedence.PRODUCT));
            ctx.infix(Symbol.OPERATOR_IMOD, new InfixBinary(operator("mod"), Operator.Precedence.PRODUCT));
            ctx.infix(Symbol.OPERATOR_EXP, new InfixBinary(operator("pow"), Operator.Precedence.EXPONENT));

            ctx.infix(Symbol.OPERATOR_FIND, new InfixBinary(operator("find"), Operator.Precedence.EXPONENT));
            ctx.prefix(Symbol.OPERATOR_SV, new NullaryOperator(operator("sv")));
        } catch(ParseError | IllegalStateException error) {
            throw new IllegalStateException("Failed to initialise PlangGrammar", error);
        }
    }

    private void createComparisons(Context ctx, Scalar booleanType, Type type) throws ParseError {
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
        return RootParser.parse(parser);
    }

    private Scalar makeScalar(Context ctx, Type archetype) throws ParseError {
        String typeName = props.getTypeName(archetype);
        Scalar type = new Scalar(typeName, archetype, archetype.getInitialization());

        for (String variant : calculateVariants(typeName)) {
            ctx.declareType(typeName(variant), type);
        }

        return type;
    }

    private OperatorName operator(String operatorName) {
        return new OperatorName(props.getFunctionName(operatorName));
    }

    private Set<Name> aliases(String name) {
        return calculateVariants(name).stream()
            .map(PlangName::name)
            .collect(toSet());
    }
}
