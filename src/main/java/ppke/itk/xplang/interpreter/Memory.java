package ppke.itk.xplang.interpreter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.VariableDeclaration;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The memory of the {@link Interpreter}.
 *
 * <p>The memory consists of an ordered list of "frames" representing the memory slices used by the active scopes.
 * Each frame is a mapping of {@link VariableDeclaration}s to {@link Value}s. For each Value a label is stored as well
 * (the original variable names), but these are only used for debugging purposes.</p>
 *
 * <p>Whenever the value of a variable is looked up, we first look at the uppermost frame (the current scope), then in
 * the lowermost one (the global scope). If the variable is not found anywhere, an exception is thrown.</p>
 */
class Memory {
    private final static Logger log = LoggerFactory.getLogger("Root.Interpreter.Memory");

    private final static class Frame {
        final String label;
        final Map<VariableDeclaration, Entry> memory = new HashMap<>();
        Map<VariableDeclaration, Entry> savedState = null;

        Frame(String label) {
            this.label = label;
        }

        void set(VariableDeclaration variable, Entry entry) {
            memory.put(variable, entry);
        }

        Optional<Entry> find(VariableDeclaration variable) {
            return Optional.ofNullable(memory.get(variable));
        }

        Optional<Entry> findOld(VariableDeclaration variable) {
            return savedState == null ? Optional.empty() : Optional.ofNullable(savedState.get(variable));
        }

        void saveState() {
            savedState = memory.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().copy()
                ));
        }

        String describe() {
            return memory.values().stream()
                .sorted(Comparator.comparing(x -> x.label))
                .map(Entry::describe)
                .collect(Collectors.joining(","));
        }
    }

    private final static class Entry {
        final String label;
        Value value;

        Entry(String label, Value value) {
            this.label = label;
            this.value = value;
        }

        Entry copy() {
            return new Entry(label, value.copy());
        }

        String describe() {
            return String.format("%s=%s", label, value);
        }
    }

    private final Deque<Frame> frames;

    /**
     * Constructor. Establishes the global scope.
     */
    Memory() {
        frames = new ArrayDeque<>();
        frames.add(new Frame("global"));
    }

    void addFrame() {
        frames.add(new Frame(Integer.toString(frames.size() - 1)));
    }

    /**
     * Saves the state of the current frame, copying all values.
     *
     * <p>The saved values can later be retrieved using the {@link #getSavedValue(VariableDeclaration)} method.</p>
     *
     * <p>Calling this method successively for the same frame overwrites the earlier saved state.
     * Closing the frame drops the saved state as well.</p>
     */
    void saveFrame() {
        frames.getLast().saveState();
    }

    /**
     * Close the uppermost frame.
     *
     * @throws IllegalStateException if there is only one open frame.
     */
    void closeFrame() {
        if (frames.size() == 1) {
            throw new IllegalStateException("Tried to close the lowermost frame (the global scope)");
        }

        frames.removeLast();
    }

    /**
     * Allocate memory to a given variable in the currently active scope.
     *
     * @param variable The memory address. Any object.
     * @param name A human-readable name for this variable.
     * @param value The initial value of the memory slot.
     * @throws IllegalStateException If the variable is already declared.
     */
    void allocate(VariableDeclaration variable, String name, Value value) throws InterpreterError {
        Frame frame = frames.getLast();
        log.debug("Allocating space for '{}' @ '{}' in frame {}", name, variable, frame.label);

        if (frame.find(variable).isPresent()) {
            log.error("Allocation failed, variable '{}' already declared in frame {}", name, frame.label);
            throw new IllegalStateException("Variable already declared");
        }

        frame.set(variable, new Entry(name, value));
    }

    /**
     * Get a reference (a pointer) to a declared variable.
     *
     * @throws IllegalStateException if the variable is not found.
     */
    ReferenceValue getReference(VariableDeclaration variable) {
        find(variable);
        return new MemoryReference(this, variable);
    }

    /**
     * Assign a Value to a variable.
     *
     * @param variable A variable to assign..
     * @param value The value to assign this variable.
     * @throws IllegalStateException if the variable is not found.
     */
    void setValue(VariableDeclaration variable, Value value) {
        Entry entry = find(variable);
        entry.value = value;
        log.debug("Updated value of '{}' to '{}'", entry.label, entry.value);
    }

    /**
     * Get the value of a stored variable
     *
     * @param variable The variable to be queried.
     * @return The value stored in the memory for the given variable.
     * @throws IllegalStateException if the given variable cannot be found.
     */
    Value getValue(VariableDeclaration variable) {
        Entry entry = find(variable);
        return entry.value;
    }

    /**
     * Get the saved state of a variable.
     *
     * <p>Must be called after {@link #saveFrame()}</p>
     *
     * @throws IllegalStateException if there is no saved state, or the variable is not in the saved state.
     */
    Value getSavedValue(VariableDeclaration variable) {
        return frames.getLast().findOld(variable)
            .orElseThrow(() -> new IllegalStateException(String.format("Variable %s has not been saved", variable.getName())))
            .value;
    }

    private Entry find(VariableDeclaration variable) {
        Frame frame = frames.getLast();
        Optional<Entry> entry = frame.find(variable);
        if (!entry.isPresent() && frames.size() > 1) {
            entry = frames.getFirst().find(variable);
        }

        if (!entry.isPresent()) {
            log.error("Variable '{}' not found in the uppermost or lowermost frame", variable);
            throw new IllegalStateException("Unknown variable");
        }

        return entry.get();
    }

    String dump() {
        return frames.getLast().describe();
    }

    @Override
    public String toString() {
        return dump();
    }
}
