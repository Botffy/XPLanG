package ppke.itk.xplang.interpreter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.*;

public class Interpreter implements ASTVisitor {
    private final static Logger log = LoggerFactory.getLogger("Root.Interpreter");

    private int memory = 0;

    @Override public void visit(Root root) {
        root.entryPoint().accept(this);
        System.out.println(String.format("Value: %s", memory));
    }

    @Override public void visit(Program program) {
        log.debug("Executing {}", program);
        program.block().accept(this);
    }

    @Override public void visit(Scope scope) {
    }

    @Override public void visit(VariableDeclaration variableDeclaration) {

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

    }

    @Override public void visit(VarRef varRef) {

    }

    @Override public void visit(VarVal varVal) {

    }

    @Override public void visit(IntegerLiteral integerLiteral) {

    }

    @Override public void visit(Incrementation incrementation) {
        log.debug("incrementing");
        ++memory;
    }

    @Override public void visit(Decrementation decrementation) {
        log.debug("decrementing");
        --memory;
    }
}
