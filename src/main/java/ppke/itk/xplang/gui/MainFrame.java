package ppke.itk.xplang.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.gui.toolkit.Button;
import ppke.itk.xplang.gui.toolkit.MenuItem;
import ppke.itk.xplang.gui.toolkit.PlangAction;
import ppke.itk.xplang.gui.toolkit.PlangIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public class MainFrame extends JFrame {
    private static final Logger log = LoggerFactory.getLogger("Root.Gui");

    private GuiState state;

    private final StatusBar statusBar;
    private final Editor editor;
    private final JFileChooser fileChooser = new JFileChooser();
    private final Map<GuiAction, Action> actions = new EnumMap<>(GuiAction.class);

    private Compiler.Result compilerResult;

    public MainFrame() {
        super();
        statusBar = new StatusBar();
        editor = new Editor(this::setTitleFrom);
        editor.addCursorPositionChangeListener(statusBar);

        actions.put(GuiAction.OPEN, PlangAction.doing(() -> this.selectFileToLoad().ifPresent(this::loadFile))
            .labelled("Fájl megnyitása")
            .withIcon(PlangIcon.OPEN)
            .withHotKey(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK))
            .build()
        );

        actions.put(GuiAction.SAVE, PlangAction.doing(() -> this.loadedFileOrSelectedFile().ifPresent(this::saveToFile))
            .labelled("Mentés")
            .withIcon(PlangIcon.SAVE)
            .withHotKey(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK))
            .build()
        );

        actions.put(GuiAction.SAVE_AS, PlangAction.doing(() -> this.selectFileToSaveTo().ifPresent(this::saveToFile))
            .labelled("Mentés másként")
            .withHotKey(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK))
            .build()
        );

        actions.put(GuiAction.COMPILE, PlangAction.doing(this::compile)
            .labelled("Programszöveg értelmezése")
            .withIcon(PlangIcon.COMPILE)
            .withHotKey(KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK))
            .build()
        );

        actions.put(GuiAction.EDIT, PlangAction.doing(this::edit)
            .labelled("Értelmezett program szerkesztése")
            .withIcon(PlangIcon.EDIT)
            .withHotKey(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK))
            .build()
        );

        actions.put(GuiAction.RUN, PlangAction.doing(this::run)
            .labelled("Futtatás")
            .withIcon(PlangIcon.RUN)
            .withHotKey(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK))
            .build()
        );

        actions.put(GuiAction.STOP, PlangAction.doing(this::stopRunning)
            .labelled("Futtatás vége")
            .withHotKey(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK))
            .withIcon(PlangIcon.STOP)
            .build()
        );

        JPanel cp = new JPanel(new BorderLayout());
        cp.add(createToolBar(), BorderLayout.PAGE_START);
        cp.add(editor.getEditorPane(), BorderLayout.CENTER);
        cp.add(statusBar.getComponent(), BorderLayout.SOUTH);
        setContentPane(cp);
        setJMenuBar(createMenuBar());

        edit();

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
        editor.focus();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void edit() {
        editor.setEditable(true);
        statusBar.setDisplayCursorInfo(true);
        statusBar.setStatusMessage("Kész.");
        setState(GuiState.EDITING);
    }

    private void compile() {
        setState(GuiState.WORKING);
        statusBar.setDisplayCursorInfo(false);
        statusBar.setStatusMessage("Fordítás...");
        editor.setEditable(false);
        Compiler compiler = new Compiler();
        compilerResult = compiler.compile(editor.getText());
        if (compilerResult.isSuccess()) {
            setState(GuiState.COMPILED);
            statusBar.setStatusMessage("Futtatásra kész.");
        } else {
            setState(GuiState.COMPILED_WITH_ERRORS);
            statusBar.setStatusMessage(
                String.format("A fordítás kész: %d hiba", compilerResult.getErrorLog().getNumberOfErrors())
            );
        }
    }

    private void run() {
        setState(GuiState.RUNNING);
        statusBar.setStatusMessage("Futtatás...");
        Executor executor = new Executor();
        executor.execute(compilerResult.getAst());
        statusBar.setStatusMessage("A program futása befejeződött.");
    }

    private void stopRunning() {
        setState(GuiState.COMPILED);
        statusBar.setStatusMessage("Futtatásra kész.");
    }

    private void setState(GuiState state) {
        for (Map.Entry<GuiAction, Action> entry : actions.entrySet()) {
            entry.getValue().setEnabled(
                state.getActiveActions().contains(entry.getKey())
            );
        }

        this.state = state;
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

    private Optional<File> loadedFileOrSelectedFile() {
        Optional<File> file = editor.getLoadedFile();
        if (!file.isPresent()) {
            file = this.selectFileToSaveTo();
        }
        return file;
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

        fileMenu.add(new MenuItem(actions.get(GuiAction.OPEN)));
        fileMenu.add(new MenuItem(actions.get(GuiAction.SAVE)));
        fileMenu.add(new MenuItem(actions.get(GuiAction.SAVE_AS)));

        fileMenu.addSeparator();
        JMenuItem item = new JMenuItem("Kilépés");
        item.addActionListener(e -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
        fileMenu.add(item);
        menuBar.add(fileMenu);

        return menuBar;
    }

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        toolBar.add(new Button(actions.get(GuiAction.OPEN)));
        toolBar.add(new Button(actions.get(GuiAction.SAVE)));
        toolBar.addSeparator();
        toolBar.add(new Button(actions.get(GuiAction.COMPILE)));
        toolBar.add(new Button(actions.get(GuiAction.EDIT)));
        toolBar.addSeparator();
        toolBar.add(new Button(actions.get(GuiAction.RUN)));
        toolBar.add(new Button(actions.get(GuiAction.STOP)));
        return toolBar;
    }
}