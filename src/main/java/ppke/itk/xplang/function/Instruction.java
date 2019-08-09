package ppke.itk.xplang.function;

import ppke.itk.xplang.type.Archetype;
import ppke.itk.xplang.type.FixArray;
import ppke.itk.xplang.type.Signature;
import ppke.itk.xplang.type.Type;

import java.util.List;

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

    /** Generate a random number between the [0..x) interval */
    RAND(Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE),

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

    /** To uppercase */
    NAGY(Archetype.CHARACTER_TYPE, Archetype.CHARACTER_TYPE),

    /** To lowercase */
    KIS(Archetype.CHARACTER_TYPE, Archetype.CHARACTER_TYPE),

    /** Is argument a letter? */
    IS_LETTER(Archetype.BOOLEAN_TYPE, Archetype.CHARACTER_TYPE),

    /** Is argument a number? */
    IS_DIGIT(Archetype.BOOLEAN_TYPE, Archetype.CHARACTER_TYPE),

    /** Array length: get the number of elements in any addressable type */
    ARLEN(Archetype.INTEGER_TYPE, Archetype.ADDRESSABLE);

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
}
