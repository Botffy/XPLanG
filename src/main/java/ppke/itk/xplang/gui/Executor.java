package ppke.itk.xplang.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.Root;
import ppke.itk.xplang.interpreter.Interpreter;
import ppke.itk.xplang.interpreter.InterpreterError;
import ppke.itk.xplang.interpreter.InterpreterStoppedException;

import javax.swing.*;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

class Executor {
    private static final Logger log = LoggerFactory.getLogger("Root.Gui.Executor");

    private final List<ExecutorListener> listeners = new ArrayList<>();
    private AtomicBoolean interpreterShouldStop = new AtomicBoolean();
    private Console console;

    void execute(Root astRoot, Console console) {
        this.console = console;
        interpreterShouldStop.set(false);
        GuiStreamHandler streamHandler = createStreamHandler(this.console);
        Thread thread = new Thread(() -> {
            log.info("Starting execution");
            Interpreter interpreter = new Interpreter(streamHandler, 10000, () -> interpreterShouldStop.get());
            try {
                interpreter.visit(astRoot);
                log.info("Finished execution");
                log.debug(interpreter.memoryDump());
                SwingUtilities.invokeLater(() -> listeners.forEach(ExecutorListener::onInterpreterFinished));
            } catch (InterpreterStoppedException e) {
                log.info("Interpreter stopped", e);
            } catch (InterpreterError e) {
                log.info("Interpreter exited with error: ", e);
                SwingUtilities.invokeLater(() -> listeners.forEach(x -> x.onInterpreterError(e.getMessage())));
            } catch (Throwable e) {
                log.error("Interpreter crashed", e);
                SwingUtilities.invokeLater(() -> listeners.forEach(x -> x.onInterpreterCrash(e)));
            } finally {
                interpreter.memoryDump();
                streamHandler.close();
            }
        }, "Interpreter");
        thread.start();
    }

    synchronized void stop() {
        log.debug("Stopping interpreter.");
        interpreterShouldStop.set(true);
        this.console.closeOutputStream();
    }

    void addExecutorListener(ExecutorListener listener) {
        listeners.add(listener);
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

    public interface ExecutorListener {
        void onInterpreterFinished();
        void onInterpreterError(String errorMessage);
        void onInterpreterCrash(Throwable e);
    }
}
