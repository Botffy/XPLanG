package ppke.itk.xplang.gui;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

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

    void receiveDataFrom(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        new Thread(new DisplayUpdater(reader, display)).start();
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

    private static class DisplayUpdater implements Runnable {
        private final BufferedReader reader;
        private final JTextArea display;

        DisplayUpdater(BufferedReader reader, JTextArea display) {
            this.reader = reader;
            this.display = display;
        }

        @Override
        public void run() {
            try {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    display.append(line + '\n');
                    display.setCaretPosition(display.getDocument().getLength());
                }
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
