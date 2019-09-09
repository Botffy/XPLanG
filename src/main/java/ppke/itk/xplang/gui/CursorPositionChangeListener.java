package ppke.itk.xplang.gui;

import ppke.itk.xplang.common.CursorPosition;

public interface CursorPositionChangeListener {
    void onCursorMovement(CursorPosition position);
    void onSelectionChange(CursorPosition start, CursorPosition end, int characters, int lines);
}
