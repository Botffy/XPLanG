package ppke.itk.xplang.function;

import ppke.itk.xplang.type.Archetype;
import ppke.itk.xplang.type.FixArray;
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

    /** Integer negation (unary minus operator): f(x) = -x. */
    INEG(Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE),

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

    ARLEN(Archetype.INTEGER_TYPE, FixArray.ANY_ARRAY);

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
