package ppke.itk.xplang.interpreter;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static ppke.itk.xplang.interpreter.ValueUtils.convert;

public class RecordValue extends AddressableValue {
    private final Map<String, Value> values;

    public RecordValue(Map<String, Value> values) {
        this.values = values;
    }

    @Override
    int size() {
        return values.size();
    }

    @Override
    public ReferenceValue getReference(Value address) throws InterpreterError {
        return new ComponentReference(this, address);
    }

    @Override
    public void setComponent(Value address, Value value) throws InterpreterError {
        values.put(getStringAddress(address), value);
    }

    @Override
    public Value getComponent(Value address) throws InterpreterError {
        return values.get(getStringAddress(address));
    }

    @Override
    public RecordValue copy() {
        Map<String, Value> newValues = new HashMap<>();
        for (Map.Entry<String, Value> entry : values.entrySet()) {
            newValues.put(entry.getKey(), entry.getValue().copy());
        }
        return new RecordValue(newValues);
    }


    @Override
    public String toString() {
        return String.format(
            "Record(%s)",
            values.entrySet().stream()
                .sorted(comparing(Map.Entry::getKey))
                .map(x -> String.format("%s=%s", x.getKey(), x.getValue()))
                .collect(joining(","))
        );
    }

    private String getStringAddress(Value value) {
        String address;
        try {
            address = convert(value, StringValue.class).getValue();
        } catch (ClassCastException e) {
            throw new IllegalStateException("Record addressed with something else than string", e);
        }

        if (!values.containsKey(address)) {
            throw new IllegalStateException("Addressed record with unknown address");
        }

        return address;
    }
}
