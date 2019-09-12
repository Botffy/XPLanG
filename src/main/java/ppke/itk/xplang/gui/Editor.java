package ppke.itk.xplang.gui;

import org.apache.commons.io.IOUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SquiggleUnderlineHighlightPainter;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.ChangeableHighlightPainter;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.common.CompilerMessage;
import ppke.itk.xplang.common.CursorPosition;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static ppke.itk.xplang.gui.util.CursorPositionConverter.toCursorPosition;
import static ppke.itk.xplang.gui.util.CursorPositionConverter.toOffset;

class Editor implements CompilerResultListener, DocumentListener, CaretListener {
    private final static Logger log = LoggerFactory.getLogger("Root.Gui.Editor");

    private final RSyntaxTextArea textArea;
    private final RTextScrollPane scrollPane;
    private final Consumer<Editor> onStateChange;
    private File file;
    private boolean isDirty;
    private List<CursorPositionChangeListener> cursorPositionChangeListeners = new ArrayList<>();

    Editor(Consumer<Editor> onStateChange) {
        this.onStateChange = onStateChange;
        this.textArea = new RSyntaxTextArea(30, 80);
        this.textArea.setTabSize(2);
        this.textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
        this.textArea.setCodeFoldingEnabled(false);
        this.textArea.getDocument().addDocumentListener(this);
        this.textArea.addCaretListener(this);
        this.scrollPane = new RTextScrollPane(textArea);
    }

    Component getEditorPane() {
        return scrollPane;
    }

    void focus() {
        textArea.requestFocusInWindow();
    }

    void setEditable(boolean editable) {
        this.textArea.setEditable(editable);
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
    public void onCompilerResult(Compiler.Result result) {
        List<CompilerMessage> errorMessages = result.getErrorLog().getErrorMessages();
        ChangeableHighlightPainter painter = new SquiggleUnderlineHighlightPainter(Color.RED);
        Highlighter highlighter = textArea.getHighlighter();
        highlighter.removeAllHighlights();
        for (CompilerMessage errorMessage : errorMessages) {
            try {
                int p0 = toOffset(textArea, errorMessage.getCursorPosition());
                int p1 = toOffset(textArea, errorMessage.getEndPosition());
                highlighter.addHighlight(p0, p1, painter);
            } catch (BadLocationException e) {
                throw new IllegalStateException(e);
            }
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

    public void addCursorPositionChangeListener(CursorPositionChangeListener listener) {
        cursorPositionChangeListeners.add(listener);
    }

    public void removeCursorPositionChangeListener(CursorPositionChangeListener listener) {
        cursorPositionChangeListeners.remove(listener);
    }

    @Override
    public void caretUpdate(CaretEvent event) {
        int dot = event.getDot();
        int mark = event.getMark();

        if (dot == mark) {
            try {
                CursorPosition cursorPosition = toCursorPosition(textArea, dot);
                cursorPositionChangeListeners.forEach(x -> x.onCursorMovement(cursorPosition));
                return;
            } catch (BadLocationException e) {
                throw new IllegalStateException(e);
            }
        }

        int start = min(dot, mark);
        int end = max(dot, mark);

        try {
            CursorPosition startPos = toCursorPosition(textArea, start);
            CursorPosition endPos = toCursorPosition(textArea, end);

            cursorPositionChangeListeners.forEach(x -> x.onSelectionChange(
                startPos, endPos, endPos.line - startPos.line, end - start
            ));
        } catch (BadLocationException e) {
            throw new IllegalStateException(e);
        }
    }
}
