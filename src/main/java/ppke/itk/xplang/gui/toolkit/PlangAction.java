package ppke.itk.xplang.gui.toolkit;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public class PlangAction extends AbstractAction {
    private final Consumer<ActionEvent> action;

    private PlangAction(Consumer<ActionEvent> action) {
        this.action = action;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        action.accept(e);
    }

    public static Builder doing(Consumer<ActionEvent> action) {
        return new Builder(action);
    }

    public static Builder doing(Runnable action) {
        return new Builder(e -> action.run());
    }

    public static class Builder {
        private String label;
        private PlangIcon icon;
        private KeyStroke acceleratorKey;
        private String shortDescription;
        private Consumer<ActionEvent> action;

        private Builder(Consumer<ActionEvent> action) {
            this.action = action;
        }

        public Builder labelled(String label) {
            this.label = label;
            return this;
        }

        public Builder withIcon(PlangIcon icon) {
            this.icon = icon;
            return this;
        }

        public Builder withHotKey(KeyStroke keyStroke) {
            this.acceleratorKey = keyStroke;
            return this;
        }

        public Builder withShortDescription(String shortDescription) {
            this.shortDescription = shortDescription;
            return this;
        }

        public Builder doing(Consumer<ActionEvent> action) {
            this.action = action;
            return this;
        }

        public PlangAction build() {
            PlangAction result = new PlangAction(action);
            result.putValue(Action.NAME, label);
            result.putValue(Action.SMALL_ICON, icon != null ? icon.getIcon() : null);
            result.putValue(Action.ACCELERATOR_KEY, acceleratorKey);
            result.putValue(Action.SHORT_DESCRIPTION, shortDescription == null ? label : shortDescription);
            return result;
        }
    }
}
