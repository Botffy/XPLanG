package ppke.itk.xplang.ui;

import ppke.itk.xplang.ast.Node;
import ppke.itk.xplang.interpreter.Interpreter;

import java.util.Collections;
import java.util.List;

public class Interpreting implements Action{
    private final Node node;

    public Interpreting(Node node) {
        this.node = node;
    }

    @Override public List<Action> execute() {
        Interpreter interpreter = new Interpreter();
        node.accept(interpreter);
        return Collections.emptyList();
    }
}
