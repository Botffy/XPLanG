package ppke.itk.xplang.gui.toolkit;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

import javax.swing.*;

public enum PlangIcon {
    SAVE(FontAwesome.FLOPPY_O),
    OPEN(FontAwesome.FOLDER_OPEN),
    COMPILE(FontAwesome.DESKTOP),
    EDIT(FontAwesome.PENCIL_SQUARE_O),
    RUN(FontAwesome.PLAY),
    STOP(FontAwesome.STOP);

    private final Icon icon;

    PlangIcon(FontAwesome iconName) {
        IconFontSwing.register(FontAwesome.getIconFont());
        this.icon = IconFontSwing.buildIcon(iconName, 18);
    }

    public Icon getIcon() {
        return icon;
    }
}
