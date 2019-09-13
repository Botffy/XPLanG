package ppke.itk.xplang.gui;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

import static java.util.Arrays.stream;

class Console {
    private final static Font MONOSPACED = new Font("monospaced", Font.PLAIN, 12);

    private final JPanel panel;
    private final JTextArea display;
    private final JTextField input;

    Console() {
        panel = new JPanel(new BorderLayout());
        display = createDisplay();
        input = createInput();

        JScrollPane scrollPane = new JScrollPane(display);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(input, BorderLayout.SOUTH);
    }

    JComponent getComponent() {
        return panel;
    }

    void receiveDataFrom(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        new Thread(new DisplayUpdater(reader, display)).start();
    }

    void sendDataTo(OutputStream os) {
        input.setEditable(true);

        stream(input.getActionListeners()).forEach(input::removeActionListener);

        input.addActionListener(ev -> {
            try {
                os.write(input.getText().getBytes(StandardCharsets.UTF_8));
                os.write('\n');
                os.flush();
                input.setText("");
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        });
    }


    private JTextArea createDisplay() {
        JTextArea result = new JTextArea(10, 80);
        result.setBackground(Color.BLACK);
        result.setForeground(Color.WHITE);
        result.setFont(MONOSPACED);
        result.setEditable(false);
        result.setLineWrap(true);
        return result;
    }

    private JTextField createInput() {
        JTextField result = new JTextField();
        result.setBackground(Color.BLACK);
        result.setForeground(Color.WHITE);
        result.setCaretColor(Color.WHITE);
        result.setFont(MONOSPACED);
        result.setEditable(false);
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
