package ppke.itk.xplang.interpreter;

import ppke.itk.xplang.function.Instruction;
import ppke.itk.xplang.util.Stack;

import java.util.EnumMap;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;

import static ppke.itk.xplang.interpreter.BooleanValue.FALSE;
import static ppke.itk.xplang.interpreter.BooleanValue.TRUE;

class InstructionProcessor {
    private static Random random = new Random();

    private static EnumMap<Instruction, Execution> executions = new EnumMap<>(Instruction.class);
    static {
        executions.put(Instruction.EQ, comparison(x -> x == 0));
        executions.put(Instruction.NEQ, comparison(x -> x != 0));
        executions.put(Instruction.LT, comparison(x -> x < 0));
        executions.put(Instruction.LTE, comparison(x -> x <= 0));
        executions.put(Instruction.GT, comparison(x -> x > 0));
        executions.put(Instruction.GTE, comparison(x -> x >= 0));
        executions.put(Instruction.NOT, new UnaryInstruction<>(BooleanValue.class, x -> x.negate()));
        executions.put(Instruction.OR, new BinaryInstruction<>(BooleanValue.class, (x, y) -> x.or(y)));
        executions.put(Instruction.AND, new BinaryInstruction<>(BooleanValue.class, (x, y) -> x.and(y)));
        executions.put(Instruction.ARLEN, new UnaryInstruction<>(AddressableValue.class, x -> new IntegerValue(x.size())));
        executions.put(Instruction.INEG, new UnaryInstruction<>(IntegerValue.class, x -> new IntegerValue(- x.getValue())));
        executions.put(Instruction.IABS, new UnaryInstruction<>(IntegerValue.class, x -> new IntegerValue(Math.abs(x.getValue()))));
        executions.put(Instruction.ISUM, integerBinary(Integer::sum));
        executions.put(Instruction.ISUB, integerBinary((x, y) -> x - y));
        executions.put(Instruction.IMUL, integerBinary((x, y) -> x * y));
        executions.put(Instruction.IDIV, integerBinary((x, y) -> x / y));
        executions.put(Instruction.IMOD, integerBinary((x, y) -> x % y));
        executions.put(Instruction.RAND, new UnaryInstruction<>(IntegerValue.class, x -> new IntegerValue(random.nextInt(x.getValue()))));
        executions.put(Instruction.FNEG, new UnaryInstruction<>(RealValue.class, x -> new RealValue(- x.getValue())));
        executions.put(Instruction.FABS, new UnaryInstruction<>(RealValue.class, x -> new RealValue(Math.abs(x.getValue()))));
        executions.put(Instruction.FSUM, realBinary(Double::sum));
        executions.put(Instruction.FSUB, realBinary((x, y) -> x - y));
        executions.put(Instruction.FMUL, realBinary((x, y) -> x * y));
        executions.put(Instruction.FDIV, realBinary((x, y) -> x / y));
        executions.put(Instruction.FEXP, realBinary(Math::pow));
        executions.put(Instruction.SIN, realUnary(Math::sin));
        executions.put(Instruction.COS, realUnary(Math::cos));
        executions.put(Instruction.TAN, realUnary(Math::tan));
        executions.put(Instruction.ASIN, realUnary(Math::asin));
        executions.put(Instruction.ACOS, realUnary(Math::acos));
        executions.put(Instruction.ATAN, realUnary(Math::atan));
        executions.put(Instruction.LD, realUnary(Math::log));
        executions.put(Instruction.EXP, realUnary(Math::exp));
    }

    static void execute(Instruction instruction, Stack<Value> stack) {
        if (!executions.containsKey(instruction)) {
            throw new IllegalStateException(String.format("Unknown instruction %s", instruction));
        }

        executions.get(instruction).execute(stack);
    }

    private static BinaryInstruction<BooleanValue, ComparableValue> comparison(Function<Integer, Boolean> function) {
        return new BinaryInstruction<>(ComparableValue.class, (x, y) -> function.apply(x.compareTo(y)) ? TRUE : FALSE);
    }

    private static BinaryInstruction<IntegerValue, IntegerValue> integerBinary(BiFunction<Integer, Integer, Integer> function) {
        return new BinaryInstruction<>(IntegerValue.class, (x, y) -> new IntegerValue(function.apply(x.getValue(), y.getValue())));
    }

    private static UnaryInstruction<RealValue, RealValue> realUnary(Function<Double, Double> function) {
        return new UnaryInstruction<>(RealValue.class, x -> new RealValue(function.apply(x.getValue())));
    }

    private static BinaryInstruction<RealValue, RealValue> realBinary(BiFunction<Double, Double, Double> function) {
        return new BinaryInstruction<>(RealValue.class, (x, y) -> new RealValue(function.apply(x.getValue(), y.getValue())));
    }

    @FunctionalInterface
    private interface Execution {
        void execute(Stack<Value> stack);
    }

    private static class UnaryInstruction<R extends Value, T extends Value> implements Execution {
        private final Class<T> operandType;
        private final Function<T, R> function;

        private UnaryInstruction(Class<T> operandType, Function<T, R> function) {
            this.operandType = operandType;
            this.function = function;
        }

        @Override
        public void execute(Stack<Value> stack) {
            T value = stack.pop(operandType);
            stack.push(function.apply(value));
        }
    }

    private static class BinaryInstruction<R extends Value, T extends Value> implements Execution {
        private final Class<T> argType;
        private final BiFunction<T, T, R> function;

        BinaryInstruction(Class<T> argType, BiFunction<T, T, R> function) {
            this.argType = argType;
            this.function = function;
        }

        @Override
        public void execute(Stack<Value> stack) {
            T right = stack.pop(argType);
            T left = stack.pop(argType);
            stack.push(function.apply(left, right));

        }
    }
}
