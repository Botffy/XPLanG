package ppke.itk.xplang.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;

class IOHandler {
    private static final Logger log = LoggerFactory.getLogger("Root.Gui.IO");

    private final JFileChooser fileChooser = new JFileChooser();

    private final JFrame parentFrame;
    private final Supplier<Editor> editorSupplier;

    IOHandler(JFrame parentFrame, Supplier<Editor> editorSupplier) {
        this.parentFrame = parentFrame;
        this.editorSupplier = editorSupplier;
    }

    boolean loseChangesConfirm(String question, String title) {
        if (!editorSupplier.get().isDirty()) {
            return true;
        }

        int confirm = JOptionPane.showConfirmDialog(
            parentFrame,
            "A programszöveg változásai nincsenek elmenetve. " + question,
            title,
            JOptionPane.YES_NO_OPTION
        );

        return confirm == JOptionPane.YES_OPTION;
    }

    Optional<File> loadedFileOrSelectedFile() {
        Optional<File> file = editorSupplier.get().getLoadedFile();
        if (!file.isPresent()) {
            file = this.selectFileToSaveTo();
        }
        return file;
    }

    Optional<File> selectFileToSaveTo() {
        if (fileChooser.showSaveDialog(parentFrame) == JFileChooser.APPROVE_OPTION) {
            return Optional.of(fileChooser.getSelectedFile());
        }
        return Optional.empty();
    }

    Optional<File> selectFileToLoad() {
        if (loseChangesConfirm("Biztosan be akarsz tölteni egy új fájlt?", "Betöltés")) {
            if (fileChooser.showOpenDialog(parentFrame) == JFileChooser.APPROVE_OPTION) {
                return Optional.of(fileChooser.getSelectedFile());
            }
        }
        return Optional.empty();
    }

    void saveToFile(File file) {
        try {
            editorSupplier.get().saveTo(file);
        } catch (IOException e) {
            log.error("Could not save to file {}", file, e);
            JOptionPane.showMessageDialog(parentFrame, "Nem tudtuk elmenteni a fájlt!");
        }
    }

    void loadFile(File file) {
        try {
            editorSupplier.get().loadFile(file);
        } catch (FileNotFoundException e) {
            log.error("Could not load file {}", file, e);
            JOptionPane.showMessageDialog(parentFrame, "A megadott file nem létezik");
        } catch (IOException e) {
            log.error("Error while trying to load file {}", file, e);
            JOptionPane.showMessageDialog(parentFrame, "Nem sikerült a file betöltése: " + e.getMessage());
        }
    }
}
