package ppke.itk.xplang.gui;

import ppke.itk.xplang.gui.toolkit.PlangIcon;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Optional;

class FileSaveAction extends AbstractAction {
    private final MainFrame frame;
    private final Editor editor;

    FileSaveAction(MainFrame frame, Editor editor) {
        super("Mentés", PlangIcon.SAVE.getIcon());
        this.frame = frame;
        this.editor = editor;
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        putValue(Action.SHORT_DESCRIPTION, "Mentés");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Optional<File> file = editor.getLoadedFile();
        if (!file.isPresent()) {
            file = frame.selectFileToSaveTo();
        }

        file.ifPresent(frame::saveToFile);
    }
}
