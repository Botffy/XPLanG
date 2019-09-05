package ppke.itk.xplang.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class FileSaveAsAction extends AbstractAction {
    private final MainFrame mainFrame;

    public FileSaveAsAction(MainFrame mainFrame) {
        super("Mentés másként");

        this.mainFrame = mainFrame;
        putValue(
            Action.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK)
        );
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        mainFrame.selectFileToSaveTo().ifPresent(mainFrame::saveToFile);
    }
}
