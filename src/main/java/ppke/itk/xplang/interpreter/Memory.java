package ppke.itk.xplang.interpreter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

class Memory {
    private final static Logger log = LoggerFactory.getLogger("Root.Interpreter.Memory");

    private final static class Entry {
        final String label;
        Value value;

        Entry(String label, Value value) {
            this.label = label;
            this.value = value;
        }

        public String describe() {
            return String.format("%s=%s", label, value);
        }

        @Override public String toString() {
            return value.toString();
        }
    }

    private final Map<Object, Entry> memory = new HashMap<Object, Entry>();

    void allocate(Object key, String name) throws InterpreterError {
        log.debug("Allocating space for '{}' @ '{}'", name, key);
        if(memory.containsKey(key)) {
            log.error("Allocation failed, address '{}' already in use");
            throw new InterpreterError("Address already in use");
        }
        memory.put(key, new Entry(name, Value.nullValue()));
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

    void set(Object key, Value value) throws InterpreterError {
        if(!memory.containsKey(key)) {
            log.error("Unknown address '{}'", key);
            throw new InterpreterError("Unknown address");
        }

        Entry entry = memory.get(key);
        entry.value = value;
        log.debug("Updated value of '{}' to '{}'", entry.label, entry.value);
    }

    void set(AddressValue addressValue, Value value) throws InterpreterError {
        set(addressValue.getAddress(), value);
    }

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
