package ppke.itk.xplang.parser;

import org.junit.Assert;
import org.junit.Test;
import ppke.itk.xplang.lang.PlangGrammar;

public class PlangGrammarTest {
    private final Grammar plang = new PlangGrammar();

    @Test public void contextSetupShouldNotThrow() {
        Context ctx = new Context();
        try {
            plang.setup(ctx);
        } catch(IllegalStateException e) {
            Assert.fail("Setup of grammar on context should not cause errors.");
        }
    }
}
