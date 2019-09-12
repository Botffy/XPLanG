package ppke.itk.xplang.gui;

import javax.swing.*;
import java.awt.*;

class Console {
    private final JPanel panel;
    private final JTextArea display;

    Console() {
        panel = new JPanel(new BorderLayout());

        display = createDisplay();

        JScrollPane scrollPane = new JScrollPane(display);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        panel.add(scrollPane, BorderLayout.CENTER);
    }

    JComponent getComponent() {
        return panel;
    }

    private JTextArea createDisplay() {
        JTextArea result = new JTextArea(10, 80);
        result.setBackground(Color.BLACK);
        result.setForeground(Color.WHITE);
        result.setFont(new Font("monospaced", Font.PLAIN, 12));
        result.setEditable(false);
        result.setLineWrap(true);
        return result;
    }
}
