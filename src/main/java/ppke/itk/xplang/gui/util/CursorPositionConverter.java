package ppke.itk.xplang.gui.util;

import ppke.itk.xplang.common.CursorPosition;

import javax.swing.*;
import javax.swing.text.BadLocationException;

/**
 * Utility methods to convert a JTextArea offset number to a {@code CursorPosition}.
 */
public class CursorPositionConverter {
    private CursorPositionConverter() { /* no-op */ }

    public static CursorPosition toCursorPosition(JTextArea textArea, int offset) throws BadLocationException {
        int line = textArea.getLineOfOffset(offset);
        int col = offset - textArea.getLineStartOffset(line);
        return new CursorPosition(line + 1, col + 1);
    }

    public static int toOffset(JTextArea textArea, CursorPosition position) throws BadLocationException {
        int lineStartOffset = textArea.getLineStartOffset(position.line - 1);
        return lineStartOffset + position.column - 1;
    }
}
