package language;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ppke.itk.xplang.ast.Root;
import ppke.itk.xplang.common.CompilerMessage;
import ppke.itk.xplang.common.ErrorLog;
import ppke.itk.xplang.lang.PlangGrammar;
import ppke.itk.xplang.parser.Grammar;
import ppke.itk.xplang.parser.Parser;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class LanguageIT {
    private final Grammar grammar = new PlangGrammar();
    private String fileName;

    public LanguageIT(String fileName) {
        this.fileName = fileName;
    }

    @Test
    public void testFile() throws IOException {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(
            LanguageIT.class.getResourceAsStream(fileName)
        ))) {
            String errorMessage = reader.readLine().substring(3);

            ErrorLog errorLog = new ErrorLog();
            Parser parser = new Parser(errorLog);
            Root root = parser.parse(reader, grammar);
            if(errorLog.isEmpty()) {
                fail(String.format("%s (%s)", errorMessage, this.fileName));
            }
        }
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        Collection<Object[]> data = new ArrayList<Object[]>();
        File dir = new File(LanguageIT.class.getResource("/plang-strict/parse-error").getFile());
        for(File file : dir.listFiles()) {
            data.add(new Object[]{ String.format("/plang-strict/parse-error/%s", file.getName() ) });
        }

        return data;
    }
}
