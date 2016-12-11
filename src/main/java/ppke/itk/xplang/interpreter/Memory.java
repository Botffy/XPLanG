package ppke.itk.xplang.interpreter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The memory of the {@link Interpreter}.
 *
 * A set of {@link Value}s mapped to arbitrary Object type addresses. The memory stores "labels" as well (the original
 * variable names), but these are only used for debugging purposes.
 */
class Memory {
    private final static Logger log = LoggerFactory.getLogger("Root.Interpreter.Memory");

    private final static class Entry {
        final String label;
        Value value;

        Entry(String label, Value value) {
            this.label = label;
            this.value = value;
        }

        String describe() {
            return String.format("%s=%s", label, value);
        }

        @Override public String toString() {
            return value.toString();
        }
    }

    private final Map<Object, Entry> memory = new HashMap<Object, Entry>();

    /**
     * Allocate memory at a given address.
     * @param key The memory address. Any object.
     * @param name A human-readable name for this memory slot.
     * @param value The initial value of the memory slot.
     * @throws InterpreterError If the address is already occupied.
     */
    void allocate(Object key, String name, Value value) throws InterpreterError {
        log.debug("Allocating space for '{}' @ '{}'", name, key);
        if(memory.containsKey(key)) {
            log.error("Allocation failed, address '{}' already in use");
            throw new InterpreterError("Address already in use");
        }
        memory.put(key, new Entry(name, value));
    }

    void deallocate(Object key) throws InterpreterError {
        log.debug("Deallocating space @ '{}'");
        if(!memory.containsKey(key)) {
            log.error("Deallocation failed, unknown address '{}'", key);
            throw new InterpreterError("Unknown address");
        }
        Entry entry = memory.remove(key);
        log.debug("Deallocated entry for {}", key);
    }

    /**
     * Set the content of a memory slot.
     * @param key The memory address.
     * @param value The value to put at this address.
     */
    void set(Object key, Value value) throws InterpreterError {
        if(!memory.containsKey(key)) {
            log.error("Unknown address '{}'", key);
            throw new InterpreterError("Unknown address");
        }

        Entry entry = memory.get(key);
        entry.value = value;
        log.debug("Updated value of '{}' to '{}'", entry.label, entry.value);
    }

    /**
     * Set the content of a memory slot referenced by a pointer.
     * @param addressValue A pointer to a memory slot.
     * @param value The value to put at this address.
     */
    void set(AddressValue addressValue, Value value) throws InterpreterError {
        set(addressValue.getAddress(), value);
    }

    /**
     * Get the value stored in a memory slot.
     * @param key The memory slot to be queried.
     * @return The value in the given memory slot. May be NullValue.
     */
    Value get(Object key) throws InterpreterError {
        if(!memory.containsKey(key)) {
            log.error("Unknown address '{}'", key);
            throw new InterpreterError("Unknown address");
        }

        Entry entry = memory.get(key);
        return entry.value;
    }

    Value dereference(AddressValue addressValue) throws InterpreterError {
        return get(addressValue.getAddress());
    }

    String dump() {
        return memory.entrySet().stream()
            .map(Map.Entry::getValue)
            .sorted(Comparator.comparing(x -> x.label))
            .map(Entry::describe)
            .collect(Collectors.joining(","));
    }

    @Override public String toString() {
        return memory.toString();
    }
}
