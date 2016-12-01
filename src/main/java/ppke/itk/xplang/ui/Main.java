package ppke.itk.xplang.ui;

public class Main {
    /**
     * CLI entry point.
     *
     * @param args The command line arguments.
     */
    public static void main(final String[] args) {
        Program program = new Program();
        program.run(new MessagePrinter("Hello XPLanG"));
    }
}
