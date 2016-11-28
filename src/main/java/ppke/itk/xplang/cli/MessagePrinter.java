package ppke.itk.xplang.cli;

import java.util.List;

import static java.util.Collections.emptyList;


class MessagePrinter implements Action {
    private final String message;

    MessagePrinter(String message) {
        this.message = message;
    }

    @Override public List<Action> execute() {
        System.out.println(this.message);
        return emptyList();
    }
}
