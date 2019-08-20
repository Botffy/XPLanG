package ppke.itk.xplang.interpreter;

import java.io.IOException;
import java.io.Writer;

public class OutputStreamValue implements Value {
    private Writer writer;

    OutputStreamValue(Writer writer) {
        this.writer = writer;
    }

    @Override
    public Value copy() {
        return new OutputStreamValue(this.writer);
    }

    void write(WritableValue writable) {
        try {
            writer.write(writable.asOutputString());
            writer.flush();
        } catch (IOException e) {
            throw new InterpreterError("Could not write to outputstream.");
        }
    }

    void printLn() {
        try {
            writer.write("\n");
            writer.flush();
        } catch (IOException e) {
            throw new InterpreterError("Could not write to outputstream.");
        }
    }
}
