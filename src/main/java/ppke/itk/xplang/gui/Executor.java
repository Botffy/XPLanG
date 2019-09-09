package ppke.itk.xplang.gui;

import ppke.itk.xplang.ast.Root;
import ppke.itk.xplang.common.StreamHandler;
import ppke.itk.xplang.interpreter.Interpreter;
import ppke.itk.xplang.ui.FileStreamHandler;

class Executor {
    void execute(Root astRoot) {
        StreamHandler streamHandler = new FileStreamHandler();
        Interpreter interpreter = new Interpreter(streamHandler);
        interpreter.visit(astRoot);
    }
}
