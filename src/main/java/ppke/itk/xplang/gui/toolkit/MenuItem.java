package ppke.itk.xplang.gui.toolkit;

import javax.swing.*;

public class MenuItem extends JMenuItem {
    public MenuItem(Action a) {
        super(a);
        this.setToolTipText("");
    }

    @Override
    public void setAction(Action a) {
        super.setAction(a);
        this.setToolTipText("");
    }
}
