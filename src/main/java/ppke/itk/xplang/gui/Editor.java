package ppke.itk.xplang.gui;

import org.apache.commons.io.IOUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Consumer;

class Editor implements DocumentListener {
    private final static Logger log = LoggerFactory.getLogger("Root.Gui.Editor");

    private final RSyntaxTextArea textArea;
    private final RTextScrollPane scrollPane;
    private final Consumer<Editor> onStateChange;
    private File file;
    private boolean isDirty;

    Editor(Consumer<Editor> onStateChange) {
        this.onStateChange = onStateChange;
        this.textArea = new RSyntaxTextArea(20, 60);
        this.textArea.setTabSize(2);
        this.textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
        this.textArea.setCodeFoldingEnabled(false);
        this.textArea.getDocument().addDocumentListener(this);
        this.scrollPane = new RTextScrollPane(textArea);
    }

    Component getEditorPane() {
        return scrollPane;
    }

    void focus() {
        textArea.requestFocusInWindow();
    }

    Optional<File> getLoadedFile() {
        return Optional.ofNullable(file);
    }

    String getText() {
        return textArea.getText();
    }

    void loadFile(File file) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            String s = IOUtils.toString(reader);
            this.textArea.setText(s);
            this.textArea.setCaretPosition(0);
            setFile(file);
            log.debug("Loaded file {}", file);
        }
    }

    void saveTo(File file) throws IOException {
        String text = textArea.getText();
        if (!text.endsWith("\n")) {
            textArea.append("\n");
            text = textArea.getText();
        }
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            writer.write(text);
            setFile(file);
            setDirty(false);
            log.debug("Saved to file {}", file);
        }
    }

    private void setFile(File file) {
        this.file = file;
        setDirty(false);
        onStateChange.accept(this);
    }

    public boolean isDirty() {
        return isDirty;
    }

    private void setDirty(boolean isDirty) {
        boolean wasDirty = this.isDirty;
        this.isDirty = isDirty;

        if (wasDirty != isDirty) {
            this.onStateChange.accept(this);
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        setDirty(true);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        setDirty(true);
    }

    @Override
    public void changedUpdate(DocumentEvent e) { /* noop */ }
}
