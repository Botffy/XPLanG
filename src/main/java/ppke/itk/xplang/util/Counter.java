package ppke.itk.xplang.util;

/**
 * A simple counter too keep track of things.
 */
public class Counter {
    private final int startingValue;
    private int iter = 0;

    public Counter(int startingValue) {
        this.startingValue = startingValue;
    }

    public Counter() {
        this.startingValue = 0;
    }

    /**
     * Advance the counter.
     * @return the current value of the counter.
     */
    synchronized public int advance() {
        return ++iter + startingValue;
    }

    /**
     * How many things have been counted so far?
     * @return the current value of the counter.
     */
    public int tally() {
        return iter + startingValue;
    }
}
