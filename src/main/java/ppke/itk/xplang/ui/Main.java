package ppke.itk.xplang.ui;

public final class Main {
    private Main() {
        // private ctor
    }

    /**
     * CLI entry point.
     *
     * @param args The command line arguments.
     */
    public static void main(final String[] args) {
        Program program = new Program();
        program.run(args);
    }
}
