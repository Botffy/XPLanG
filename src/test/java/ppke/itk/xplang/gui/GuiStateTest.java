package ppke.itk.xplang.gui;

import org.junit.Test;

import java.util.EnumSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class GuiStateTest {
    @Test
    public void allActionsCovered() {
        Set<GuiAction> set = EnumSet.allOf(GuiAction.class);
        for (GuiState state : GuiState.values()) {
            set.removeAll(state.getActiveActions());
        }

        assertTrue("Every action should be enabled in at least one state", set.isEmpty());
    }
}
