package ppke.itk.xplang.gui;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

enum GuiState {
    EDITING(GuiAction.OPEN, GuiAction.SAVE, GuiAction.SAVE_AS, GuiAction.COMPILE),
    COMPILED(GuiAction.EDIT, GuiAction.RUN),
    RUNNING(GuiAction.STOP);

    private final List<GuiAction> activeActions;
    GuiState(GuiAction... action) {
        activeActions = unmodifiableList(asList(action));
    }

    public List<GuiAction> getActiveActions() {
        return activeActions;
    }
}
