package ppke.itk.xplang.ui;

import ppke.itk.xplang.ast.ASTPrinter;
import ppke.itk.xplang.ast.Node;

import java.util.Collections;
import java.util.List;

public class ASTPrinting implements Action {
    private final Node node;

    public ASTPrinting(Node node) {
        this.node = node;
    }

    @Override public List<Action> execute() {
        ASTPrinter printer = new ASTPrinter();
        printer.visit(node);
        return Collections.emptyList();
    }
}
