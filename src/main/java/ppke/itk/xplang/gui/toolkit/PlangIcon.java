package ppke.itk.xplang.gui.toolkit;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;

public enum PlangIcon {
    SAVE(FontIcon.of(FontAwesomeSolid.SAVE)),
    OPEN(FontIcon.of(FontAwesomeSolid.FOLDER_OPEN));

    private final Icon icon;

    PlangIcon(Icon icon) {
        this.icon = icon;
    }

    public Icon getIcon() {
        return icon;
    }
}
