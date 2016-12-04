package ppke.itk.xplang.ui;

import java.io.File;

/**
 * Configures the program's behaviour.
 */
class RunConfig {
    private final Program.Action action;
    private final File sourceFile;

    RunConfig(Program.Action action, File sourceFile) {
        this.action = action;
        this.sourceFile = sourceFile;
    }

    /**
     * The course of action the program should take.
     * @return
     */
    Program.Action getAction() {
        return action;
    }

    /**
     * Where is the source code the program should operate on?
     * @return the File object representing the source code. The file is weakly guaranteed to exist and be readable.
     *         If the {@code name} property of the File is set to '-', that is a signal the program should read the
     *         source code from the standard input stream.
     */
    File getSourceFile() {
        return sourceFile;
    }
}
