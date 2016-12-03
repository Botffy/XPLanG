package ppke.itk.xplang.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CounterTest {
    @Test
    public void counterShouldCountWhenAdvanced() {
        Counter counter = new Counter();
        assertEquals(1, counter.advance());
        assertEquals(2, counter.advance());
        assertEquals(3, counter.advance());
        assertEquals(4, counter.advance());
    }

    @Test
    public void counterTallyShouldHoldResult() {
        Counter counter = new Counter();
        counter.advance();
        counter.advance();
        counter.advance();
        assertEquals(3, counter.tally());
        assertEquals(3, counter.tally());
    }

    @Test
    public void initialValue() {
        Counter counter = new Counter(100);
        assertEquals(100, counter.tally());
        assertEquals(101, counter.advance());
        assertEquals(102, counter.advance());
        assertEquals(102, counter.tally());
    }
}
