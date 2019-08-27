package ppke.itk.xplang.interpreter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * An input for a program.
 */
public class ProgramInput {
    private static final Logger log = LoggerFactory.getLogger("Root.Interpreter");

    private final Reader reader;
    private final String name;

    public ProgramInput(Reader reader, String name) {
        if (!reader.markSupported()) {
            reader = new BufferedReader(reader);
        }

        this.reader = reader;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Integer readInt() {
        throwIfNotOpen();
        skipWs();
        if (isExhausted()) {
            return null;
        }
        return Integer.parseInt(readNumber());
    }

    public Double readReal() {
        throwIfNotOpen();
        skipWs();
        if (isExhausted()) {
            return null;
        }

        StringBuilder builder = new StringBuilder(readNumber());

        int dot = peek();
        if (dot == '.') {
            builder.append((char) get());
            builder.append(readNumber());
        }
        return Double.parseDouble(builder.toString());
    }

    public String readLine() {
        throwIfNotOpen();
        if (isExhausted()) {
            return null;
        }

        StringBuilder line = new StringBuilder();

        int c = get();
        while (c != -1 && c != '\n' && c != '\r') {
            line.append((char) c);
            c = get();
        }

        if (c == '\r' && peek() == '\n') {
            get();
        }

        return line.toString();
    }

    public Character readCharacter() {
        throwIfNotOpen();
        if (isExhausted()) {
            return null;
        }

        return (char) get();
    }

    public Boolean readBoolean() {
        throwIfNotOpen();
        skipWs();
        if (isExhausted()) {
            return null;
        }

        char c = Character.toLowerCase(readCharacter());
        if (c == 'h' || c == 'f') {
            return false;
        }
        if (c == 'i' || c == 'y') {
            return true;
        }
        throw new BadInputException();
    }

    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            throw new InterpreterError("Could not close stream");
        }
    }

    public boolean isExhausted() {
        return peek() == -1;
    }

    private boolean isClosed() {
        return this.reader == null;
    }

    private void throwIfNotOpen() {
        if (isClosed()) {
            throw new UnopenedStreamException();
        }
    }

    private int get() {
        try {
            return reader.read();
        } catch (IOException e) {
            throw new BadInputException(e);
        }
    }

    private String readNumber() {
        StringBuilder builder = new StringBuilder();
        int c = peek();
        if (c == '-') {
            builder.append((char) get());
        }

        char digit = readDigit();
        builder.append(digit);

        int nextDigit = peek();
        while (nextDigit >= '0' && nextDigit <= '9') {
            builder.append(readDigit());
            nextDigit = peek();
        }

        return builder.toString();
    }

    private int peek() {
        try {
            reader.mark(1);
            int c = get();
            reader.reset();
            return c;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void skipWs() {
        int c = peek();
        while (Character.isWhitespace(c)) {
            get();
            c = peek();
        }
    }

    private char readDigit() {
        int c = get();
        if ((c < '0' || c > '9') && c != -1) {
            log.warn("Tried to read a digit, found '{}'", c);
            throw new BadInputException();
        }
        return (char) c;
    }
}
