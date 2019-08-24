package ppke.itk.xplang.interpreter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

public class InputStreamValue implements Value {
    private static final Logger log = LoggerFactory.getLogger("Root.Interpreter");

    private Reader originalReader;
    private PushbackReader reader;

    InputStreamValue() { }

    InputStreamValue(Reader reader) {
        setReader(reader);
    }

    public void setReader(Reader reader) {
        this.originalReader = reader;
        this.reader = new PushbackReader(reader);
    }

    public int readInt() {
        throwIfNotOpen();
        skipWs();
        return Integer.parseInt(readNumber());
    }

    public double readReal() {
        throwIfNotOpen();
        skipWs();
        StringBuilder builder = new StringBuilder(readNumber());

        char dot = (char) get();
        if (dot == '.') {
            builder.append(dot);
            builder.append(readNumber());
        } else {
            unread(dot);
        }
        return Double.parseDouble(builder.toString());
    }

    public String readLine() {
        throwIfNotOpen();
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

    public char readCharacter() {
        throwIfNotOpen();
        return (char) get();
    }

    public boolean readBoolean() {
        throwIfNotOpen();
        skipWs();
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
        if (isClosed()) {
            return;
        }

        try {
            reader.close();
            this.reader = null;
            this.originalReader = null;
        } catch (IOException e) {
            throw new InterpreterError("Could not close stream", e);
        }
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
        char c = (char) get();
        if (c == '-') {
            builder.append(c);
        } else {
            unread(c);
        }

        builder.append(readDigit());

        char nextDigit = peek();
        while (nextDigit >= '0' && nextDigit <= '9') {
            builder.append(readDigit());
            nextDigit = peek();
        }

        return builder.toString();
    }

    private char peek() {
        char c = (char) get();
        unread(c);
        return c;
    }

    private void skipWs() {
        char c = (char) get();
        while (Character.isWhitespace(c)) {
            c = (char) get();
        }
        unread(c);
    }

    private void unread(char c) {
        try {
            reader.unread(c);
        } catch (IOException e) {
            throw new IllegalStateException();
        }
    }

    private char readDigit() {
        char c = (char) get();
        if (c < '0' || c > '9') {
            log.warn("Tried to read a digit, found '{}'", c);
            throw new BadInputException();
        }
        return c;
    }

    public Value copy() {
        return isClosed() ? new InputStreamValue() : new InputStreamValue(originalReader);
    }

    public String toString() {
        return isClosed() ? "ClosedInputStream" : "OpenInputStream";
    }
}
