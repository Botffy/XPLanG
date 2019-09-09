package ppke.itk.xplang.gui;

import ppke.itk.xplang.common.CursorPosition;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;

import static java.lang.String.format;

class StatusBar implements CursorPositionChangeListener {
    private static final Integer MIN_WIDTH = 100;
    private static final Integer MIN_HEIGHT = 16;

    private final JPanel panel;
    private final JLabel message;

    private boolean displayCursorInfo = false;

    StatusBar() {
        this.panel = new JPanel();
        panel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        panel.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        message = new JLabel("Status");
        message.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(message);
    }

    Component getComponent() {
        return panel;
    }

    void setStatusMessage(String message) {
        this.message.setText(message);
    }

    void setDisplayCursorInfo(boolean display) {
        this.displayCursorInfo = display;
    }

    @Override
    public void onCursorMovement(CursorPosition position) {
        if (!displayCursorInfo) {
            return;
        }

        this.message.setText(format("%d:%d", position.line + 1, position.column + 1));
    }

    @Override
    public void onSelectionChange(CursorPosition start, CursorPosition end, int lines, int characters) {
        if (!displayCursorInfo) {
            return;
        }

        if (lines == 0) {
            this.message.setText(format("%d karakter", characters));
        } else {
            this.message.setText(format("%d sor, %d karakter", lines + 1, characters));
        }
    }
}
