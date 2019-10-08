package ppke.itk.xplang.gui;

import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.ToolTipSupplier;
import ppke.itk.xplang.common.CompilerMessage;
import ppke.itk.xplang.common.CompilerMessageTranslator;
import ppke.itk.xplang.common.CursorPosition;

import javax.swing.text.BadLocationException;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import static ppke.itk.xplang.gui.util.CursorPositionConverter.toCursorPosition;

class CompilerMessageToolTipSupplier implements ToolTipSupplier {
    private final List<CompilerMessage> messages = new ArrayList<>();
    private final CompilerMessageTranslator translator;

    CompilerMessageToolTipSupplier(CompilerMessageTranslator translator) {
        this.translator = translator;
    }

    void clear() {
        messages.clear();
    }

    public void add(CompilerMessage message) {
        this.messages.add(message);
    }

    @Override
    public String getToolTipText(RTextArea textArea, MouseEvent e) {
        try {
            int offset = textArea.viewToModel(e.getPoint());
            CursorPosition cursorPosition = toCursorPosition(textArea, offset);

            for (CompilerMessage message : messages) {
                if (message.getLocation().contains(cursorPosition)) {
                    return translator.translate(message);
                }
            }

            return null;
        } catch (BadLocationException ex) {
            return null;
        }
    }
}
