package ppke.itk.xplang.gui.toolkit;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;

public enum PlangIcon {
    SAVE(FontIcon.of(FontAwesomeSolid.SAVE)),
    OPEN(FontIcon.of(FontAwesomeSolid.FOLDER_OPEN)),
    COMPILE(FontIcon.of(FontAwesomeSolid.DESKTOP)),
    EDIT(FontIcon.of(FontAwesomeSolid.EDIT)),
    RUN(FontIcon.of(FontAwesomeSolid.PLAY)),
    STOP(FontIcon.of(FontAwesomeSolid.STOP));

    private final Icon icon;

    PlangIcon(Icon icon) {
        this.icon = icon;
    }

    public Icon getIcon() {
        return icon;
    }
}
