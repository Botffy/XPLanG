package ppke.itk.xplang.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ErrorLog {
    private final List<CompilerMessage> messages = new ArrayList<>();

    public void add(CompilerMessage error) {
        messages.add(error);
    }

    public List<CompilerMessage> getErrorMessages() {
        return Collections.unmodifiableList(messages);
    }

    public long getNumberOfErrors() {
        return messages.stream()
            .filter(x -> x.getSeverity() == CompilerMessage.Severity.ERROR)
            .count();
    }

    public boolean hasNoErrors() {
        return messages.stream().noneMatch(x -> x.getSeverity() == CompilerMessage.Severity.ERROR);
    }

    public boolean isEmpty() {
        return messages.isEmpty();
    }

    @Override
    public String toString() {
        return messages.toString();
    }
}
