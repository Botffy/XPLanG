package ppke.itk.xplang.ui;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class OptionParserTest {
    private OptionParser parser;

    @Before
    public void setUp() {
        this.parser = new OptionParser();
    }

    @Test
    public void getHelp() {
        RunConfig config = parser.parseOptions(new String[]{"--help"});
        assertSame("Requesting the help screen should result in NONE action",
            Program.Action.NONE, config.getAction()
        );

        config = parser.parseOptions(new String[]{"--help", "example.plang"});
        assertSame("The --help switch and a source file should result in NONE action",
            Program.Action.NONE, config.getAction()
        );
    }

    @Test
    public void getVersion() {
        RunConfig config = parser.parseOptions(new String[]{"--version"});
        assertSame("Requesting the version screen should result in NONE action",
            Program.Action.NONE, config.getAction()
        );

        config = parser.parseOptions(new String[]{"--version", "example.prog"});
        assertSame("The --version switch and a source file should result in NONE action",
            Program.Action.NONE, config.getAction()
        );
    }

    @Test
    public void specifyingSourceFiles() {
        RunConfig config = parser.parseOptions(new String[]{"example.prog"});
        assertSame("Existing file as source input should be accepted, and should trigger the default action",
            Program.Action.getDefaultAction(), config.getAction()
        );
        assertEquals("Existing file as source input should be accepted.",
            "example.prog", config.getSourceFile().getName()
        );
    }

    @Test
    public void stdInSource() {
        RunConfig config = parser.parseOptions(new String[]{"-"});
        assertSame("- as source input should be accepted, and should trigger the default action",
            Program.Action.getDefaultAction(), config.getAction()
        );
        assertEquals("- as source input should be accepted (representing the standard input)",
            new File("-"), config.getSourceFile()
        );
    }

    @Test
    public void errors() {
        RunConfig config = parser.parseOptions(new String[]{"--invalid", "example.prog"});
        assertSame("Invalid options should result in a NONE action",
            Program.Action.NONE, config.getAction()
        );

        config = parser.parseOptions(new String[]{"nonesuch.prog"});
        assertSame("Specifying nonexistent files should result in a NONE action",
            Program.Action.NONE, config.getAction()
        );
    }
}
