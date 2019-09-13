package ppke.itk.xplang.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.Root;
import ppke.itk.xplang.interpreter.Interpreter;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

class Executor {
    private static final Logger log = LoggerFactory.getLogger("Root.Gui.Executor");

    void execute(Root astRoot, Console console) {
        GuiStreamHandler streamHandler = createStreamHandler(console);
        new Thread(() -> {
            log.info("Started execution");
            Interpreter interpreter = new Interpreter(streamHandler);
            interpreter.visit(astRoot);
            streamHandler.close();
            log.info("Finished execution");
            log.debug(interpreter.memoryDump());
        }).start();
    }

    private GuiStreamHandler createStreamHandler(Console console) {
        try {
            PipedInputStream input = new PipedInputStream();
            PipedOutputStream pipedOutputStream = new PipedOutputStream(input);
            console.receiveDataFrom(input);

            PipedOutputStream output = new PipedOutputStream();
            PipedInputStream pipedInputStream = new PipedInputStream(output);
            console.sendDataTo(output);

            return new GuiStreamHandler(pipedOutputStream, pipedInputStream);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
