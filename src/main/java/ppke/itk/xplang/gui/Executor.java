package ppke.itk.xplang.gui;

import ppke.itk.xplang.ast.Root;
import ppke.itk.xplang.common.StreamHandler;
import ppke.itk.xplang.interpreter.Interpreter;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

class Executor {
    void execute(Root astRoot, Console console) {
        GuiStreamHandler streamHandler = createStreamHandler(console);
        new Thread(() -> {
            Interpreter interpreter = new Interpreter(streamHandler);
            interpreter.visit(astRoot);
            streamHandler.close();
        }).start();
    }

    private GuiStreamHandler createStreamHandler(Console console) {
        try {
            PipedInputStream pipedInputStream = new PipedInputStream();
            PipedOutputStream pipedOutputStream = new PipedOutputStream(pipedInputStream);
            console.receiveDataFrom(pipedInputStream);
            return new GuiStreamHandler(pipedOutputStream);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
