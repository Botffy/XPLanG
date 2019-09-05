package ppke.itk.xplang.gui.toolkit;

import javax.swing.*;

public class Button extends JButton {
    public Button(Action a) {
        super(a);
        this.setHideActionText(true);
    }
}
