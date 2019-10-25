package ppke.itk.xplang.function;

import ppke.itk.xplang.type.Archetype;
import ppke.itk.xplang.type.Type;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;

/**
 * Built-in instructions.
 *
 * These are the equivalents of the instruction set of the processor: atomic functions independent of the type system
 * of a language.
 */
public enum Instruction {
    /** Identity function: f(x) = x. */
    ID(Archetype.ANY, Archetype.ANY),

    /** Equality */
    EQ(Archetype.ANY, Archetype.ANY, Archetype.ANY),

    /** Not equal */
    NEQ(Archetype.ANY, Archetype.ANY, Archetype.ANY),

    /** Comparison: less than */
    LT(Archetype.ANY, Archetype.ANY, Archetype.ANY),

    /** Comparison: greater than */
    GT(Archetype.ANY, Archetype.ANY, Archetype.ANY),

    /** Comparison: less than or equal */
    LTE(Archetype.ANY, Archetype.ANY, Archetype.ANY),

    /** Comparison: greater than or equal */
    GTE(Archetype.ANY, Archetype.ANY, Archetype.ANY),

    /** Logical NOT */
    NOT(Archetype.BOOLEAN_TYPE, Archetype.BOOLEAN_TYPE),

    /** Logical OR */
    OR(Archetype.BOOLEAN_TYPE, Archetype.BOOLEAN_TYPE, Archetype.BOOLEAN_TYPE),

    /** Logical AND */
    AND(Archetype.BOOLEAN_TYPE, Archetype.BOOLEAN_TYPE, Archetype.BOOLEAN_TYPE),

    /** Integer negation (unary minus operator): f(x) = -x. */
    INEG(Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE),

    /** Absolute value for integers */
    IABS(Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE),

    /** Integer addition. */
    ISUM(Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE),

    /** Integer subtraction */
    ISUB(Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE),

    /** Integer multiplication. */
    IMUL(Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE),

    /** Integer division. */
    IDIV(Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE),

    /** Modulus. */
    IMOD(Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE),

    /** Integer exponentiation. */
    IEXP(Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE),

    /** Generate a random number between the [0..x) interval */
    RAND(Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE),

    /** Convert integer to real number */
    ITOF(Archetype.REAL_TYPE, Archetype.INTEGER_TYPE),

    /** Sinus */
    SIN(Archetype.REAL_TYPE, Archetype.REAL_TYPE),

    /** Cosinus */
    COS(Archetype.REAL_TYPE, Archetype.REAL_TYPE),

    /** Tangent */
    TAN(Archetype.REAL_TYPE, Archetype.REAL_TYPE),

    /** Arcus sinus*/
    ASIN(Archetype.REAL_TYPE, Archetype.REAL_TYPE),

    /** Arcus cosinus */
    ACOS(Archetype.REAL_TYPE, Archetype.REAL_TYPE),

    /** Arcus tangent */
    ATAN(Archetype.REAL_TYPE, Archetype.REAL_TYPE),

    /** Base 2 logarithm */
    LD(Archetype.REAL_TYPE, Archetype.REAL_TYPE),

    /** Exponential */
    EXP(Archetype.REAL_TYPE, Archetype.REAL_TYPE),

    /** Floating point negation. */
    FNEG(Archetype.REAL_TYPE, Archetype.REAL_TYPE),

    /** Floating point absolute value. */
    FABS(Archetype.REAL_TYPE, Archetype.REAL_TYPE),

    /** Floating point addition. */
    FSUM(Archetype.REAL_TYPE, Archetype.REAL_TYPE, Archetype.REAL_TYPE),

    /** Floating point subtraction. */
    FSUB(Archetype.REAL_TYPE, Archetype.REAL_TYPE, Archetype.REAL_TYPE),

    /** Floating point multiplication. */
    FMUL(Archetype.REAL_TYPE, Archetype.REAL_TYPE, Archetype.REAL_TYPE),

    /** Floating point division. */
    FDIV(Archetype.REAL_TYPE, Archetype.REAL_TYPE, Archetype.REAL_TYPE),

    /** Floating point exponentiation. */
    FEXP(Archetype.REAL_TYPE, Archetype.REAL_TYPE, Archetype.REAL_TYPE),

    /** Round a real number r to the highest integer x for which x &lt; r */
    FTOI(Archetype.INTEGER_TYPE, Archetype.REAL_TYPE),

    /** Round a real number to the nearest integer. */
    ROUND(Archetype.INTEGER_TYPE, Archetype.REAL_TYPE),

    /** To uppercase */
    NAGY(Archetype.CHARACTER_TYPE, Archetype.CHARACTER_TYPE),

    /** To lowercase */
    KIS(Archetype.CHARACTER_TYPE, Archetype.CHARACTER_TYPE),

    /** Is argument a letter? */
    IS_LETTER(Archetype.BOOLEAN_TYPE, Archetype.CHARACTER_TYPE),

    /** Is argument a number? */
    IS_DIGIT(Archetype.BOOLEAN_TYPE, Archetype.CHARACTER_TYPE),

    /** Append character to string */
    APPEND(Archetype.STRING_TYPE, Archetype.STRING_TYPE, Archetype.CHARACTER_TYPE),

    /** Prepend a character to a string */
    PREPEND(Archetype.STRING_TYPE, Archetype.CHARACTER_TYPE, Archetype.STRING_TYPE),

    /** String concatenation */
    CONCAT(Archetype.STRING_TYPE, Archetype.STRING_TYPE, Archetype.STRING_TYPE),

    /** Find first location of a character in a string */
    FIND_CHAR(Archetype.INTEGER_TYPE, Archetype.STRING_TYPE, Archetype.CHARACTER_TYPE),

    /** Find first location of a substring in a string */
    FIND_SUBSTR(Archetype.INTEGER_TYPE, Archetype.STRING_TYPE, Archetype.STRING_TYPE),

    /** Get a newline character */
    NEWLINE(Archetype.CHARACTER_TYPE),

    /** Array length: get the number of elements in any addressable type */
    ARLEN(Archetype.INTEGER_TYPE, Archetype.ADDRESSABLE),

    /** Read an integer from the standard input. */
    IREAD(Archetype.INTEGER_TYPE),

    // TODO add inputstream as argument for the reading ops.
    /** Read a float from an sinput */
    FREAD(Archetype.REAL_TYPE),

    /** Read a boolean from an input */
    CREAD(Archetype.CHARACTER_TYPE),

    /** Read a boolean from an input */
    BREAD(Archetype.BOOLEAN_TYPE),

    /** Read a string from an input */
    SREAD(Archetype.STRING_TYPE),

    /** Read an array from an input */
    AREAD(Archetype.STRING_TYPE),

    /** Open the given file as an input stream */
    IFILE_OPEN(Archetype.INSTREAM_TYPE, Archetype.STRING_TYPE),

    /** Close the given input stream */
    IFILE_CLOSE(Archetype.INSTREAM_TYPE, Archetype.INSTREAM_TYPE),

    /** Is an input stream exhausted? */
    IFILE_END(Archetype.BOOLEAN_TYPE, Archetype.INSTREAM_TYPE),

    /** Open the given file as an input stream */
    OFILE_OPEN(Archetype.OUTSTREAM_TYPE, Archetype.STRING_TYPE),

    /** Close the given input stream */
    OFILE_CLOSE(Archetype.OUTSTREAM_TYPE, Archetype.OUTSTREAM_TYPE);

    private static final Set<Instruction> impureInstructions = EnumSet.of(
        RAND,
        FREAD,
        CREAD,
        BREAD,
        AREAD,
        IFILE_OPEN,
        IFILE_CLOSE,
        IFILE_END,
        OFILE_OPEN,
        OFILE_CLOSE
    );

    private final Type returnType;
    private final List<Type> operands;

    Instruction(Type returns, Type operand) {
        this.returnType = returns;
        this.operands = singletonList(operand);
    }

    Instruction(Type returns, Type left, Type right) {
        this.returnType = returns;
        this.operands = unmodifiableList(asList(left, right));
    }

    Instruction(Type returns, Type... args) {
        this.returnType = returns;
        this.operands = unmodifiableList(asList(args));
    }

    /**
     * The actual return type of the instruction may be this type, or any type it accepts.
     */
    public Type returnType() {
        return returnType;
    }

    /**
     * The actual operand types of the instruction may be the types these types accept.
     */
    public List<Type> operands() {
        return operands;
    }

    public boolean isPure() {
        return !impureInstructions.contains(this);
    }
}
