package ppke.itk.xplang.gui;

import javax.swing.*;
import java.awt.*;

/**
 * The right panel of the GUI that contains the {@link Editor}, and the collapsible lower panel that holds the
 * {@link ErrorLogPanel} or the {@link Console}.
 */
class RightPane {
    private final static String ERROR_PANEL = "ERROR_PANEL";
    private final static String CONSOLE_PANEL = "CONSOLE_PANEL";

    private final JPanel cards;
    private final CardLayout cardLayout;
    private final JSplitPane splitPane;

    private boolean isHidden;

    RightPane(JComponent editorPanel, JComponent errorPanel, JComponent consolePanel) {
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);
        cards.add(consolePanel, CONSOLE_PANEL);
        cards.add(errorPanel, ERROR_PANEL);

        editorPanel.setMinimumSize(new Dimension(0, 300));
        cards.setMinimumSize(new Dimension(0, 0));

        this.splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editorPanel, cards);
        splitPane.setOneTouchExpandable(true);
        hideBottomPanel();
    }

    JComponent getPane() {
        return splitPane;
    }

    void onStateChange(GuiState state) {
        switch (state) {
            case WORKING:
            case EDITING:
                hideBottomPanel();
                break;
            case COMPILED_WITH_ERRORS:
                showBottomPanel();
                cardLayout.show(cards, ERROR_PANEL);
                break;
            case COMPILED:
                showBottomPanel();
                cardLayout.show(cards, CONSOLE_PANEL);
                break;
            case RUNNING:
                break;
        }
    }

    private void hideBottomPanel() {
        if (this.isHidden) {
            return;
        }

        splitPane.getRightComponent().setVisible(false);
        splitPane.setDividerSize(0);
        splitPane.setDividerLocation(1.0d);
        this.isHidden = true;
    }

    private void showBottomPanel() {
        if (!this.isHidden) {
            return;
        }

        splitPane.getRightComponent().setVisible(true);
        splitPane.setDividerSize((Integer) UIManager.get("SplitPane.dividerSize"));
        splitPane.setDividerLocation(0.8d);
        this.isHidden = false;
    }
}
