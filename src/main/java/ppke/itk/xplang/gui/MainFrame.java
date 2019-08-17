package ppke.itk.xplang.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

public class MainFrame extends JFrame {
    private static final Logger log = LoggerFactory.getLogger("Root.Gui");

    private final Editor editor;
    private final JFileChooser fileChooser = new JFileChooser();

    public MainFrame() {
        super();
        JPanel cp = new JPanel(new BorderLayout());
        editor = new Editor(this::setTitleFrom);
        cp.add(editor.getEditorPane());
        setContentPane(cp);
        setJMenuBar(createMenuBar());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setDefaultCloseOperation(EXIT_ON_CLOSE);
                if (!MainFrame.this.loseChangesConfirm("Biztosan ki akarsz lépni?", "Kilépés")) {
                    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                }
            }
        });
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private boolean loseChangesConfirm(String question, String title) {
        if (!editor.isDirty()) {
            return true;
        }

        int confirm = JOptionPane.showConfirmDialog(
            MainFrame.this,
            "A programszöveg változásai nincsenek elmenetve. " + question,
            title,
            JOptionPane.YES_NO_OPTION
        );

        return confirm == JOptionPane.YES_OPTION;
    }

    private void setTitleFrom(Editor editor) {
        String fileName = editor.getLoadedFile()
            .map(File::getName)
            .orElse("(nem mentett");

        String dirtMark = editor.isDirty() ? " *" : "";

        this.setTitle(String.format("PLanG [%s%s]", fileName, dirtMark));
    }

    private Optional<File> selectFileToSaveTo() {
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            return Optional.of(fileChooser.getSelectedFile());
        }
        return Optional.empty();
    }

    private Optional<File> selectFileToLoad() {
        if (loseChangesConfirm("Biztosan be akarsz tölteni egy új fájlt?", "Betöltés")) {
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                return Optional.of(fileChooser.getSelectedFile());
            }
        }
        return Optional.empty();
    }

    private void saveToFile(File file) {
        try {
            editor.saveTo(file);
        } catch (IOException e) {
            log.error("Could not save to file {}", file, e);
            JOptionPane.showMessageDialog(this, "Nem tudtuk elmenteni a fájlt!");
        }
    }

    public void loadFile(File file) {
        try {
            editor.loadFile(file);
        } catch (FileNotFoundException e) {
            log.error("Could not load file {}", file, e);
            JOptionPane.showMessageDialog(this, "A megadott file nem létezik");
        } catch (IOException e) {
            log.error("Error while trying to load file {}", file, e);
            JOptionPane.showMessageDialog(this, "Nem sikerült a file betöltése: " + e.getMessage());
        }
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Fájl");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem item = new JMenuItem("Fájl megnyitása", KeyEvent.VK_O);
        item.addActionListener(e -> this.selectFileToLoad().ifPresent(this::loadFile));
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        fileMenu.add(item);

        item = new JMenuItem("Mentés");
        item.addActionListener(e -> {
            Optional<File> file = editor.getLoadedFile();
            if (!file.isPresent()) {
                file = this.selectFileToSaveTo();
            }

            file.ifPresent(this::saveToFile);
        });
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        fileMenu.add(item);

        item = new JMenuItem("Mentés másként...");
        item.addActionListener(e -> this.selectFileToSaveTo().ifPresent(this::saveToFile));
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
        fileMenu.add(item);

        fileMenu.addSeparator();
        item = new JMenuItem("Kilépés");
        item.addActionListener(e -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
        fileMenu.add(item);
        menuBar.add(fileMenu);

        return menuBar;
    }
}
