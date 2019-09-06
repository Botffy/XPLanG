package ppke.itk.xplang.gui;

import ppke.itk.xplang.gui.toolkit.PlangIcon;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

class FileOpenAction extends AbstractAction {
    private final MainFrame mainFrame;

    FileOpenAction(MainFrame mainFrame) {
        super("Fájl megnyitása", PlangIcon.OPEN.getIcon());
        this.mainFrame = mainFrame;
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        putValue(Action.SHORT_DESCRIPTION, "Fájl megnyitása");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        mainFrame.selectFileToLoad().ifPresent(mainFrame::loadFile);
    }
}
