package ppke.itk.xplang.interpreter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.common.StreamHandler;
import ppke.itk.xplang.function.Instruction;
import ppke.itk.xplang.util.Stack;

import java.io.FileNotFoundException;
import java.util.EnumMap;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;

import static ppke.itk.xplang.interpreter.BooleanValue.FALSE;
import static ppke.itk.xplang.interpreter.BooleanValue.TRUE;

class InstructionProcessor {
    private final static Logger log = LoggerFactory.getLogger("Root.Interpreter");

    private final Random random = new Random();
    private final EnumMap<Instruction, Execution> executions = new EnumMap<>(Instruction.class);

    InstructionProcessor(StreamHandler streamHandler) {
        executions.put(Instruction.EQ, comparison(x -> x == 0));
        executions.put(Instruction.NEQ, comparison(x -> x != 0));
        executions.put(Instruction.LT, comparison(x -> x < 0));
        executions.put(Instruction.LTE, comparison(x -> x <= 0));
        executions.put(Instruction.GT, comparison(x -> x > 0));
        executions.put(Instruction.GTE, comparison(x -> x >= 0));
        executions.put(Instruction.NOT, new UnaryInstruction<>(BooleanValue.class, BooleanValue::negate));
        executions.put(Instruction.OR, new BinaryInstruction<>(BooleanValue.class, BooleanValue.class, BooleanValue::or));
        executions.put(Instruction.AND, new BinaryInstruction<>(BooleanValue.class, BooleanValue.class, BooleanValue::and));
        executions.put(Instruction.ARLEN, new UnaryInstruction<>(AddressableValue.class, x -> new IntegerValue(x.size())));
        executions.put(Instruction.INEG, new UnaryInstruction<>(IntegerValue.class, x -> new IntegerValue(- x.getValue())));
        executions.put(Instruction.IABS, new UnaryInstruction<>(IntegerValue.class, x -> new IntegerValue(Math.abs(x.getValue()))));
        executions.put(Instruction.ISUM, integerBinary(Integer::sum));
        executions.put(Instruction.ISUB, integerBinary((x, y) -> x - y));
        executions.put(Instruction.IMUL, integerBinary((x, y) -> x * y));
        executions.put(Instruction.IDIV, integerBinary((x, y) -> x / y));
        executions.put(Instruction.IMOD, integerBinary((x, y) -> x % y));
        executions.put(Instruction.IEXP, integerBinary((x, y) -> (int) Math.pow((int) x, (int) y)));
        executions.put(Instruction.RAND, new UnaryInstruction<>(IntegerValue.class, x -> new IntegerValue(random.nextInt(x.getValue()))));
        executions.put(Instruction.ITOF, new UnaryInstruction<>(IntegerValue.class, x -> new RealValue(x.getValue())));
        executions.put(Instruction.FNEG, new UnaryInstruction<>(RealValue.class, x -> new RealValue(- x.getValue())));
        executions.put(Instruction.FABS, new UnaryInstruction<>(RealValue.class, x -> new RealValue(Math.abs(x.getValue()))));
        executions.put(Instruction.FSUM, realBinary(Double::sum));
        executions.put(Instruction.FSUB, realBinary((x, y) -> x - y));
        executions.put(Instruction.FMUL, realBinary((x, y) -> x * y));
        executions.put(Instruction.FDIV, realBinary((x, y) -> x / y));
        executions.put(Instruction.FEXP, realBinary(Math::pow));
        executions.put(Instruction.ROUND, new UnaryInstruction<>(RealValue.class, x -> new IntegerValue((int) Math.round(x.getValue()))));
        executions.put(Instruction.FTOI, new UnaryInstruction<>(RealValue.class, x -> new IntegerValue(x.getValue().intValue())));
        executions.put(Instruction.SIN, realUnary(Math::sin));
        executions.put(Instruction.COS, realUnary(Math::cos));
        executions.put(Instruction.TAN, realUnary(Math::tan));
        executions.put(Instruction.ASIN, realUnary(Math::asin));
        executions.put(Instruction.ACOS, realUnary(Math::acos));
        executions.put(Instruction.ATAN, realUnary(Math::atan));
        executions.put(Instruction.LD, realUnary(Math::log));
        executions.put(Instruction.EXP, realUnary(Math::exp));
        executions.put(Instruction.NAGY, new UnaryInstruction<>(CharacterValue.class, x -> new CharacterValue(Character.toUpperCase(x.getValue()))));
        executions.put(Instruction.KIS, new UnaryInstruction<>(CharacterValue.class, x -> new CharacterValue(Character.toLowerCase(x.getValue()))));
        executions.put(Instruction.IS_LETTER, new UnaryInstruction<>(CharacterValue.class, x -> BooleanValue.valueOf(Character.isLetter(x.getValue()))));
        executions.put(Instruction.IS_DIGIT, new UnaryInstruction<>(CharacterValue.class, x -> BooleanValue.valueOf(Character.isDigit(x.getValue()))));
        executions.put(Instruction.APPEND, new BinaryInstruction<>(StringValue.class, CharacterValue.class, (x, y) -> x.append(y)));
        executions.put(Instruction.PREPEND, new BinaryInstruction<>(CharacterValue.class, StringValue.class, (x, y) -> y.prepend(x)));
        executions.put(Instruction.CONCAT, new BinaryInstruction<>(StringValue.class, StringValue.class, (x, y) -> x.concat(y)));
        executions.put(Instruction.FIND_CHAR, new BinaryInstruction<>(StringValue.class, CharacterValue.class, StringValue::findCharacter));
        executions.put(Instruction.FIND_SUBSTR, new BinaryInstruction<>(StringValue.class, StringValue.class, StringValue::findSubstring));
        executions.put(Instruction.IREAD, new UnaryInstruction<>(InputStreamValue.class, InputStreamValue::readIntegerValue));
        executions.put(Instruction.FREAD, new UnaryInstruction<>(InputStreamValue.class, InputStreamValue::readRealValue));
        executions.put(Instruction.CREAD, new UnaryInstruction<>(InputStreamValue.class, InputStreamValue::readCharacterValue));
        executions.put(Instruction.BREAD, new UnaryInstruction<>(InputStreamValue.class, InputStreamValue::readBooleanValue));
        executions.put(Instruction.SREAD, new UnaryInstruction<>(InputStreamValue.class, InputStreamValue::readStringValue));
        executions.put(Instruction.IFILE_OPEN, new UnaryInstruction<>(StringValue.class, x -> {
            String fileName = x.getValue();
            try {
                return new InputStreamValue(streamHandler.getFileInput(fileName));
            } catch (FileNotFoundException e) {
                throw new InterpreterError(ErrorCode.FAILED_TO_OPEN_FILE, e, fileName);
            }
        }));
        executions.put(Instruction.IFILE_CLOSE, stack -> {
            InputStreamValue stream = stack.pop(InputStreamValue.class);
            stream.close();
            stack.push(stream);
        });
        executions.put(Instruction.IFILE_END, new UnaryInstruction<>(InputStreamValue.class, x -> {
            if (x.isClosed()) {
                throw new InterpreterError(ErrorCode.STREAM_NOT_OPEN);
            }
            return BooleanValue.valueOf(x.hasBeenExhausted());
        }));
        executions.put(Instruction.OFILE_OPEN, new UnaryInstruction<>(StringValue.class, x -> {
            String fileName = x.getValue();
            try {
                return new OutputStreamValue(streamHandler.getFileOutput(fileName));
            } catch (FileNotFoundException e) {
                throw new InterpreterError(ErrorCode.FAILED_TO_OPEN_FILE, e, fileName);
            }
        }));
        executions.put(Instruction.OFILE_CLOSE, stack -> {
            OutputStreamValue stream = stack.pop(OutputStreamValue.class);
            stream.close();
            stack.push(stream);
        });
    }

    void execute(Instruction instruction, Stack<Value> stack) {
        if (!executions.containsKey(instruction)) {
            throw new IllegalStateException(String.format("Unknown instruction %s", instruction));
        }
        log.debug("Call instruction {}", instruction);
        executions.get(instruction).execute(stack);
    }

    private static BinaryInstruction<BooleanValue, ComparableValue, ComparableValue> comparison(Function<Integer, Boolean> function) {
        return new BinaryInstruction<>(ComparableValue.class, ComparableValue.class, (x, y) -> function.apply(x.compareTo(y)) ? TRUE : FALSE);
    }

    private static BinaryInstruction<IntegerValue, IntegerValue, IntegerValue> integerBinary(BiFunction<Integer, Integer, Integer> function) {
        return new BinaryInstruction<>(IntegerValue.class, IntegerValue.class, (x, y) -> new IntegerValue(function.apply(x.getValue(), y.getValue())));
    }

    private static UnaryInstruction<RealValue, RealValue> realUnary(Function<Double, Double> function) {
        return new UnaryInstruction<>(RealValue.class, x -> new RealValue(function.apply(x.getValue())));
    }

    private static BinaryInstruction<RealValue, RealValue, RealValue> realBinary(BiFunction<Double, Double, Double> function) {
        return new BinaryInstruction<>(RealValue.class, RealValue.class, (x, y) -> new RealValue(function.apply(x.getValue(), y.getValue())));
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

    private static class BinaryInstruction<R extends Value, U extends Value, V extends Value> implements Execution {
        private final Class<U> arg1Type;
        private final Class<V> arg2Type;
        private final BiFunction<U, V, R> function;

        BinaryInstruction(Class<U> arg1Type, Class<V> arg2Type, BiFunction<U, V, R> function) {
            this.arg1Type = arg1Type;
            this.arg2Type = arg2Type;
            this.function = function;
        }

        @Override
        public void execute(Stack<Value> stack) {
            V right = stack.pop(arg2Type);
            U left = stack.pop(arg1Type);
            stack.push(function.apply(left, right));
        }
    }
}
