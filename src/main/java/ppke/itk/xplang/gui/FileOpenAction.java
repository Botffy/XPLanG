package ppke.itk.xplang.gui;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class FileOpenAction extends AbstractAction {
    private final MainFrame mainFrame;

    public FileOpenAction(MainFrame mainFrame) {
        super("Fájl megnyitása", FontIcon.of(FontAwesomeSolid.FOLDER_OPEN));
        this.mainFrame = mainFrame;
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        putValue(Action.SHORT_DESCRIPTION, "Fájl megnyitása");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        mainFrame.selectFileToLoad().ifPresent(mainFrame::loadFile);
    }
}
