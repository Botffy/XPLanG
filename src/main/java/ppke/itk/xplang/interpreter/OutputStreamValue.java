package ppke.itk.xplang.interpreter;

import java.io.IOException;
import java.io.Writer;

public class OutputStreamValue implements Value {
    private Writer writer;

    OutputStreamValue() { }

    OutputStreamValue(Writer writer) {
        setWriter(writer);
    }

    void setWriter(Writer writer) {
        this.writer = writer;
    }

    @Override
    public Value copy() {
        return new OutputStreamValue(this.writer);
    }

    void write(WritableValue writable) {
        throwIfClosed();
        try {
            writer.write(writable.asOutputString());
            writer.flush();
        } catch (IOException e) {
            throw new InterpreterError("Could not write to outputstream.");
        }
    }

    void printLn() {
        throwIfClosed();
        try {
            writer.write("\n");
            writer.flush();
        } catch (IOException e) {
            throw new InterpreterError("Could not write to outputstream.");
        }
    }

    void close() {
        if (isClosed()) {
            return;
        }

        try {
            writer.flush();
            writer.close();
            this.writer = null;
        } catch (IOException e) {
            throw new InterpreterError("Could not close stream", e);
        }
    }

    private boolean isClosed() {
        return this.writer == null;
    }

    private void throwIfClosed() {
        if (this.isClosed()) {
            throw new UnopenedStreamException();
        }
    }

    @Override
    public String toString() {
        return isClosed() ? "ClosedOutputStream" : "OutputStream";
    }
}
