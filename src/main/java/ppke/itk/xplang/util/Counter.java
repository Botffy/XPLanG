package ppke.itk.xplang.util;

/**
 * A simple counter too keep track of things.
 *
 * Most definitely not threadsafe.
 */
public class Counter {
    private final int startingValue;
    private int iter = 0;

    /**
     * Initialise the counter with a starting value. The counter will count upwards from this value.
     * @param startingValue the starting value of the counter.
     */
    public Counter(int startingValue) {
        this.startingValue = startingValue;
    }

    /**
     * Initialise the counter with a starting value of zero.
     */
    public Counter() {
        this(0);
    }

    /**
     * Advance the counter.
     * @return the current value of the counter.
     */
    public int advance() {
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
