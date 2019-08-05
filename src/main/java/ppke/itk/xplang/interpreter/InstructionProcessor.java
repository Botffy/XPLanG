package ppke.itk.xplang.interpreter;

import ppke.itk.xplang.function.Instruction;
import ppke.itk.xplang.util.Stack;

import java.util.EnumMap;
import java.util.function.BiFunction;
import java.util.function.Function;

public class InstructionProcessor {
    private static EnumMap<Instruction, Execution> executions = new EnumMap<>(Instruction.class);
    static {
        executions.put(Instruction.ARLEN, new UnaryInstruction<>(AddressableValue.class, x -> new IntegerValue(x.size())));
        executions.put(Instruction.INEG, new UnaryInstruction<>(IntegerValue.class, x -> new IntegerValue(- x.getValue())));
        executions.put(Instruction.ISUB, integerBinary((x, y) -> x - y));
        executions.put(Instruction.ISUM, integerBinary((x, y) -> x + y));
        executions.put(Instruction.IMUL, integerBinary((x, y) -> x * y));
        executions.put(Instruction.IDIV, integerBinary((x, y) -> x / y));
        executions.put(Instruction.IMOD, integerBinary((x, y) -> x % y));
    }

    public static void execute(Instruction instruction, Stack<Value> stack) {
        if (!executions.containsKey(instruction)) {
            throw new IllegalStateException(String.format("Unknown instruction %s", instruction));
        }

        executions.get(instruction).execute(stack);
    }


    private static BinaryInstruction<IntegerValue, IntegerValue> integerBinary(BiFunction<Integer, Integer, Integer> function) {
        return new BinaryInstruction<>(IntegerValue.class, (x, y) -> new IntegerValue(function.apply(x.getValue(), y.getValue())));
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
