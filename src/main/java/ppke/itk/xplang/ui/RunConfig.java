package ppke.itk.xplang.ui;

import java.io.File;

/**
 * Configures the program's behaviour.
 */
class RunConfig {
    private final Program.Action action;
    private File sourceFile;
    private boolean printAst;
    private  boolean dumpMemory;

    RunConfig(Program.Action action) {
        this.action = action;
    }

    /**
     * The course of action the program should take.
     */
    Program.Action getAction() {
        return action;
    }

    public void setSourceFile(File sourceFile) {
        this.sourceFile = sourceFile;
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

    public void shouldPrintAst(boolean printAst) {
        this.printAst = printAst;
    }

    boolean shouldPrintAst() {
        return printAst;
    }

    public void shouldDumpMemory(boolean dumpMemory) {
        this.dumpMemory = dumpMemory;
    }

    boolean shouldDumpMemory() {
        return dumpMemory;
    }

}
