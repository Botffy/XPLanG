package ppke.itk.xplang.interpreter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.*;
import ppke.itk.xplang.util.Stack;

public class Interpreter implements ASTVisitor {
    private final static Logger log = LoggerFactory.getLogger("Root.Interpreter");

    private final Memory memory = new Memory();
    private final Stack<Value> valueStack = new Stack<>();

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

    @Override public void visit(VariableDeclaration variableDeclaration) {
        memory.allocate(variableDeclaration, variableDeclaration.getName());
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

        AddressValue address = valueStack.pop(AddressValue.class);
        Value value = valueStack.pop();
        memory.set(address, value);
    }

    @Override public void visit(Conditional conditional) {
        conditional.getCondition().accept(this);
        Value value = valueStack.pop();
        if(value.equals(BooleanValue.TRUE)) {
            conditional.getSequence().accept(this);
        }
    }

    @Override public void visit(VarRef varRef) {
        valueStack.push(new AddressValue(varRef.getVariable()));
    }

    @Override public void visit(VarVal varVal) {
        valueStack.push(memory.get(varVal.getVariable()));
    }

    @Override public void visit(IntegerLiteral integerLiteral) {
        valueStack.push(new IntegerValue(integerLiteral.getValue()));
    }

    @Override public void visit(BooleanLiteral booleanLiteral) {
        valueStack.push(BooleanValue.valueOf(booleanLiteral.getValue()));
    }

    public String memoryDump() {
        return this.memory.dump();
    }
}
