package ppke.itk.xplang.interpreter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.*;
import ppke.itk.xplang.common.StreamHandler;
import ppke.itk.xplang.util.Stack;

import static ppke.itk.xplang.interpreter.ValueUtils.initialise;

public class Interpreter implements ASTVisitor {
    private final static Logger log = LoggerFactory.getLogger("Root.Interpreter");

    private final StreamHandler streamHandler;
    private final Memory memory = new Memory();
    private final Stack<Value> valueStack = new Stack<>();

    public Interpreter(StreamHandler streamHandler) {
        this.streamHandler = streamHandler;
    }

    @Override public void visit(Root root) {
        root.entryPoint().accept(this);

        System.out.println(valueStack);
        System.out.println(memory);
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

    @Override public void visit(VariableDeclaration variable) {
        memory.allocate(
            variable,
            variable.getName(),
            initialise(variable.getType())
        );
    }

    @Override public void visit(BuiltinFunction function) {
        InstructionProcessor.execute(function.getInstruction(), valueStack);
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

    @Override public void visit(Assignment assignment) {
        assignment.getRHS().accept(this);
        assignment.getLHS().accept(this);

        ReferenceValue ref = valueStack.pop(ReferenceValue.class);
        Value value = valueStack.pop();
        ref.assign(value.copy());
    }

    @Override public void visit(Conditional conditional) {
        conditional.getCondition().accept(this);
        Value value = valueStack.pop();
        log.debug("Conditional condition evaulated to {}", value);
        if(value.equals(BooleanValue.TRUE)) {
            conditional.getTrueSequence().accept(this);
        } else {
            conditional.getElseSequence().accept(this);
        }
    }

    @Override
    public void visit(Loop loop) {
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
    }

    @Override public void visit(FunctionCall call) {
        for(RValue argument : call.arguments()) {
            argument.accept(this);
        }
        call.getDeclaration().accept(this);
    }

    @Override public void visit(VarRef varRef) {
        valueStack.push(memory.getReference(varRef.getVariable()));
    }

    @Override public void visit(ElementRef elementRef) {
        elementRef.getAddress().accept(this);
        elementRef.getAddressable().accept(this);

        Addressable addressable = valueStack.pop(AddressableValue.class);
        Value address = valueStack.pop();

        valueStack.push(addressable.getReference(address));
    }

    @Override public void visit(VarVal varVal) {
        valueStack.push(memory.getComponent(varVal.getVariable()));
    }

    @Override public void visit(ElementVal elementVal) {
        elementVal.getAddress().accept(this);
        elementVal.getAddressable().accept(this);

        Addressable addressable = valueStack.pop(AddressableValue.class);
        Value address = valueStack.pop();
        valueStack.push(addressable.getComponent(address));
    }

    @Override
    public void visit(Slice slice) {
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

    public String memoryDump() {
        return this.memory.dump();
    }
}
