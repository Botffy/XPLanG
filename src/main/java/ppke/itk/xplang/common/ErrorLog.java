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

    public boolean isEmpty() {
        return messages.isEmpty();
    }
}
