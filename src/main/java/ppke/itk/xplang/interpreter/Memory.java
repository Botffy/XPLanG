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
     * @param address The memory address. Any object.
     * @param name A human-readable name for this memory slot.
     * @param value The initial value of the memory slot.
     * @throws IllegalStateException If the address is already occupied.
     */
    void allocate(Object address, String name, Value value) throws InterpreterError {
        log.debug("Allocating space for '{}' @ '{}'", name, address);
        if(memory.containsKey(address)) {
            log.error("Allocation failed, address '{}' already in use", address);
            throw new IllegalStateException("Address already in use");
        }
        memory.put(address, new Entry(name, value));
    }

    ReferenceValue getReference(Object address) throws InterpreterError {
        return new MemoryAddress(this, address);
    }

    /**
     * Set the content of a memory slot.
     * @param address The memory address.
     * @param value The value to put at this address.
     */
    void setComponent(Object address, Value value) throws InterpreterError {
        if(!memory.containsKey(address)) {
            log.error("Unknown address '{}'", address);
            throw new IllegalStateException("Unknown address");
        }

        Entry entry = memory.get(address);
        entry.value = value;
        log.debug("Updated value of '{}' to '{}'", entry.label, entry.value);
    }

    /**
     * Set the content of a memory slot referenced by a pointer.
     * @param memoryAddress A pointer to a memory slot.
     * @param value The value to put at this address.
     */
    void set(MemoryAddress memoryAddress, Value value) throws InterpreterError {
        setComponent(memoryAddress.getAddress(), value);
    }

    /**
     * Get the value stored in a memory slot.
     * @param address The memory slot to be queried.
     * @return The value in the given memory slot.
     * @throws IllegalStateException if the given address does not exist.
     * @throws InterpreterError with errorCode NULL_ERROR if the value would be null.
     */
    Value getComponent(Object address) throws InterpreterError, IllegalStateException {
        if(!memory.containsKey(address)) {
            log.error("Unknown address '{}'", address);
            throw new IllegalStateException("Unknown address");
        }

        Value value = memory.get(address).value;
        if (ValueUtils.nullValue() == value) {
            throw new InterpreterError(ErrorCode.NULL_ERROR);
        }

        return value;
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
