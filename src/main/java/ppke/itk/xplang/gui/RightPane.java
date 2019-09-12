package ppke.itk.xplang.gui;

import javax.swing.*;
import java.awt.*;

/**
 * The right panel of the GUI that contains the {@link Editor}, and the collapsible lower panel that holds the
 * {@link ErrorLogPanel}.
 */
class RightPane {
    private final JSplitPane splitPane;

    RightPane(Editor editor, ErrorLogPanel errorLogPanel) {
        Component editorPane = editor.getEditorPane();
        editorPane.setMinimumSize(new Dimension(0, 300));
        JPanel errorPane = errorLogPanel.getPanel();
        errorPane.setMinimumSize(new Dimension(0, 0));

        this.splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editorPane, errorPane);
        splitPane.setOneTouchExpandable(true);

        hideErrorPanel();
    }

    JComponent getPane() {
        return splitPane;
    }

    void hideErrorPanel() {
        splitPane.getRightComponent().setVisible(false);
        splitPane.setDividerSize(0);
        splitPane.setDividerLocation(1.0d);
    }

    void showErrorPanel() {
        if (splitPane.getRightComponent().isVisible()) {
            return;
        }
        splitPane.getRightComponent().setVisible(true);
        splitPane.setDividerSize((Integer) UIManager.get("SplitPane.dividerSize"));
        splitPane.setDividerLocation(0.8d);
    }
}
