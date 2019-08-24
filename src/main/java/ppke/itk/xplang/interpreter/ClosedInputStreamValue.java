package ppke.itk.xplang.interpreter;

public class ClosedInputStreamValue implements InputStreamValue {
    @Override
    public int readInt() {
        throw new UnopenedStreamException();
    }

    @Override
    public double readReal() {
        throw new UnopenedStreamException();
    }

    @Override
    public String readLine() {
        throw new UnopenedStreamException();
    }

    @Override
    public char readCharacter() {
        throw new UnopenedStreamException();
    }

    @Override
    public boolean readBoolean() {
        throw new UnopenedStreamException();
    }

    @Override
    public void close() { }

    @Override
    public Value copy() {
        return this;
    }

    @Override
    public String toString() {
        return "ClosedInputStream";
    }
}
