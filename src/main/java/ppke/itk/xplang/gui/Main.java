package ppke.itk.xplang.gui;

import javax.swing.*;
import java.io.File;

public class Main {
    private Main() {}

    public static void main(String[] args) {
        if (args.length > 0) {
            SwingUtilities.invokeLater(() -> new MainFrame(new File(args[0])));
        } else {
            SwingUtilities.invokeLater(MainFrame::new);
        }
    }
}
