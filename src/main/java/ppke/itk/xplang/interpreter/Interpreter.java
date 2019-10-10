package ppke.itk.xplang.interpreter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.*;
import ppke.itk.xplang.common.StreamHandler;
import ppke.itk.xplang.util.Stack;

import java.util.function.Supplier;

import static ppke.itk.xplang.interpreter.ValueUtils.initialise;

public class Interpreter implements ASTVisitor {
    private final static Logger log = LoggerFactory.getLogger("Root.Interpreter");

    private final InstructionProcessor instructionProcessor;
    private final Memory memory = new Memory();
    private final Stack<Value> valueStack = new Stack<>();

    private final OutputStreamValue stdOut;
    private final InputStreamValue stdIn;
    private int executionLimit;
    private final Supplier<Boolean> stopConditionSupplier;
    private int steps = 0;

    public Interpreter(StreamHandler streamHandler) {
        this(streamHandler, 10000, () -> false);
    }

    public Interpreter(StreamHandler streamHandler, int executionLimit, Supplier<Boolean> stopConditionSupplier) {
        this.instructionProcessor = new InstructionProcessor(streamHandler);
        this.stdOut = new OutputStreamValue(streamHandler.getStandardOutput());
        this.stdIn = new InputStreamValue(streamHandler.getStandardInput());
        this.executionLimit = executionLimit;
        this.stopConditionSupplier = stopConditionSupplier;
    }

    @Override public void visit(Root root) throws InterpreterError {
        root.entryPoint().accept(this);
        this.stdOut.printLn();
    }

    @Override public void visit(Program program) {
        log.debug("Executing {}", program);
        program.block().accept(this);
    }

    @Override public void visit(Scope scope) {
        for(VariableDeclaration var : scope.variables()) {
            var.accept(this);
        }
    }

    @Override public void visit(VariableDeclaration variable) throws InterpreterError {
        memory.allocate(
            variable,
            variable.getName(),
            initialise(variable.getType())
        );
    }

    @Override public void visit(BuiltinFunction function) {
        instructionProcessor.execute(function.getInstruction(), valueStack);
    }

    @Override
    public void visit(Function function) {

    }

    @Override public void visit(Sequence sequence) {
        for(Statement statement : sequence.statements()) {
            statement.accept(this);
        }
    }

    @Override public void visit(Block block) {
        block.scope().accept(this);
        block.sequence().accept(this);
    }

    @Override public void visit(Assignment assignment) throws InterpreterError {
        checkStopCondition();

        assignment.getRHS().accept(this);
        assignment.getLHS().accept(this);

        ReferenceValue ref = valueStack.pop(ReferenceValue.class);
        Value value = valueStack.pop();
        ref.assign(value.copy());

        step();
    }

    @Override public void visit(Conditional conditional) throws InterpreterError {
        checkStopCondition();

        boolean resolved = false;
        for (Conditional.Branch branch : conditional.getBranches()) {
            branch.getCondition().accept(this);
            BooleanValue value = valueStack.pop(BooleanValue.class);
            log.debug("Conditional condition evaulated to {}", value);
            if (value.equals(BooleanValue.TRUE)) {
                resolved = true;
                branch.getSequence().accept(this);
                break;
            }
        }

        if (!resolved) {
            conditional.getElseSequence().accept(this);
        }

        step();
    }

    @Override
    public void visit(Loop loop) throws InterpreterError {
        checkStopCondition();

        switch(loop.getType()) {
            case TEST_FIRST: {
                loop.getCondition().accept(this);
                Value value = valueStack.pop();
                log.debug("Loop condition evaulated to {}", value);
                while (value.equals(BooleanValue.TRUE)) {
                    loop.getSequence().accept(this);
                    loop.getCondition().accept(this);
                    value = valueStack.pop();
                    log.debug("Loop condition evaulated to {}", value);
                }
            } break;
            case TEST_LAST:{
                Value value;
                do {
                    loop.getSequence().accept(this);
                    loop.getCondition().accept(this);
                    value = valueStack.pop();
                    log.debug("Loop condition evaulated to {}", value);
                } while (value.equals(BooleanValue.TRUE));
            } break;
        }
        log.debug("Exit loop");

        step();
    }

    @Override
    public void visit(Input input) throws InterpreterError {
        checkStopCondition();

        for (Assignment assignment : input.getAssignments()) {
            assignment.accept(this);
        }

        step();
    }

    @Override
    public void visit(Output output) throws InterpreterError {
        checkStopCondition();

        output.getOutputStream().accept(this);
        OutputStreamValue outputStream = valueStack.pop(OutputStreamValue.class);
        for (RValue val : output.getOutputs()) {
            val.accept(this);
            WritableValue value = valueStack.pop(WritableValue.class);
            outputStream.write(value);
        }

        step();
    }

    @Override
    public void visit(ErrorRaising errorRaising) throws InterpreterError {
        errorRaising.getErrorMessage().accept(this);
        StringValue message = valueStack.pop(StringValue.class);
        throw new InterpreterError(ErrorCode.ASSERTION_FAILURE, message.getValue());
    }

    @Override public void visit(FunctionCall call) {
        for(RValue argument : call.arguments()) {
            argument.accept(this);
        }
        call.getDeclaration().accept(this);
    }

    @Override
    public void visit(ConditionalConnective conditionalConnective) {
        boolean val = conditionalConnective.operator().stopValue;

        conditionalConnective.getLeft().accept(this);
        boolean eval = valueStack.pop(BooleanValue.class).getValue();
        if (eval == val) {
            valueStack.push(BooleanValue.valueOf(val));
        } else {
            conditionalConnective.getRight().accept(this);
            eval = valueStack.pop(BooleanValue.class).getValue();
            if (eval == val) {
                valueStack.push(BooleanValue.valueOf(val));
            } else {
                valueStack.push(BooleanValue.valueOf(!val));
            }
        }
    }

    @Override public void visit(VarRef varRef) throws InterpreterError {
        valueStack.push(memory.getReference(varRef.getVariable()));
    }

    @Override public void visit(ElementRef elementRef) throws InterpreterError {
        elementRef.getAddress().accept(this);
        elementRef.getAddressable().accept(this);

        Addressable addressable = valueStack.pop(AddressableValue.class);
        Value address = valueStack.pop();

        valueStack.push(addressable.getReference(address));
    }

    @Override public void visit(VarVal varVal) throws InterpreterError {
        Value value = memory.getComponent(varVal.getVariable());
        if (value == ValueUtils.nullValue()) {
            throw new InterpreterError(ErrorCode.NULL_ERROR);
        }
        valueStack.push(value);
    }

    @Override public void visit(ElementVal elementVal) throws InterpreterError {
        elementVal.getAddress().accept(this);
        elementVal.getAddressable().accept(this);

        Addressable addressable = valueStack.pop(AddressableValue.class);
        Value address = valueStack.pop();

        Value value = addressable.getComponent(address);
        if (value == ValueUtils.nullValue()) {
            throw new InterpreterError(ErrorCode.NULL_ERROR);
        }
        valueStack.push(value);
    }

    @Override
    public void visit(Slice slice) throws InterpreterError {
        slice.getSlicable().accept(this);
        slice.getStartIndex().accept(this);
        slice.getEndIndex().accept(this);

        IntegerValue endIndex = valueStack.pop(IntegerValue.class);
        IntegerValue startIndex = valueStack.pop(IntegerValue.class);
        SlicableValue slicableValue = valueStack.pop(SlicableValue.class);
        valueStack.push(slicableValue.getSlice(startIndex, endIndex));
    }

    @Override public void visit(IntegerLiteral integerLiteral) {
        valueStack.push(new IntegerValue(integerLiteral.getValue()));
    }

    @Override public void visit(RealLiteral realLiteral) {
        valueStack.push(new RealValue(realLiteral.getValue()));
    }

    @Override public void visit(BooleanLiteral booleanLiteral) {
        valueStack.push(BooleanValue.valueOf(booleanLiteral.getValue()));
    }

    @Override public void visit(CharacterLiteral characterLiteral) {
        valueStack.push(new CharacterValue(characterLiteral.getValue()));
    }

    @Override public void visit(StringLiteral stringLiteral) {
        valueStack.push(new StringValue(stringLiteral.getValue()));
    }

    @Override
    public void visit(StandardInput standardInput) {
        valueStack.push(stdIn);
    }

    @Override
    public void visit(StandardOutput standardOutput) {
        valueStack.push(stdOut);
    }

    public String memoryDump() {
        return this.memory.dump();
    }

    private void checkStopCondition() throws InterpreterStoppedException {
        if (stopConditionSupplier.get()) {
            throw new InterpreterStoppedException();
        }
    }

    private void step() throws InterpreterError {
        ++steps;

        if (steps >= executionLimit) {
            throw new InterpreterError(ErrorCode.EXECUTION_LIMIT_REACHED, executionLimit);
        }
    }
}
