package language;

import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.Root;
import ppke.itk.xplang.common.ErrorLog;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.interpreter.Interpreter;
import ppke.itk.xplang.lang.PlangGrammar;
import ppke.itk.xplang.parser.Grammar;
import ppke.itk.xplang.parser.Parser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class LanguageIT {
    private final static Logger log = LoggerFactory.getLogger(LanguageIT.class);

    private final Grammar grammar = new PlangGrammar();
    private String fileName;
    private File file;
    private final boolean shouldPass;

    public LanguageIT(String fileName, File file, boolean shouldPass) {
        this.fileName = fileName;
        this.file = file;
        this.shouldPass = shouldPass;
    }

    @Test
    public void testFile() throws IOException {
        log.info("Parsing {}", fileName);
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(
            new FileInputStream(file), StandardCharsets.UTF_8
        ))) {
            reader.mark(1024);
            String errorMessage = reader.readLine().substring(3);
            String expectedMemory = null;
            Location firstErrorLoc = null;

            String line = reader.readLine();
            do {
                line = line.substring(2);

                if(line.startsWith("Memory:")) {
                    expectedMemory = line.substring(7);
                    log.info(expectedMemory);
                } else if(line.startsWith("FirstErrorLoc:")) {
                    line = line.substring(14);
                    String[] loc = line.split(",");
                    firstErrorLoc = new Location(Integer.parseInt(loc[0]), Integer.parseInt(loc[1]));
                }
                line = reader.readLine();
            } while(line.startsWith("**"));
            reader.reset();

            ErrorLog errorLog = new ErrorLog();
            Parser parser = new Parser(errorLog);
            Root root = parser.parse(reader, grammar);
            if(!shouldPass) {
                if(errorLog.isEmpty()) {
                    fail(String.format("%s (%s)", errorMessage, this.fileName));
                }
                if(firstErrorLoc != null) {
                    assertEquals(firstErrorLoc, errorLog.getErrorMessages().get(0).getLocation());
                }

            } else {
                if(!errorLog.isEmpty()) {
                    fail(String.format("%s (%s)", errorMessage, this.fileName));
                }

                Interpreter interpreter = new Interpreter();
                interpreter.visit(root);

                if(expectedMemory != null) {
                    assertEquals("Memory dump should match the expected one",
                        expectedMemory, interpreter.memoryDump()
                    );
                }
            }
        }
    }

    public static final Map<String, Boolean> directories = new HashMap<>();
    static {
        directories.put("parse-error", false);
        directories.put("passing", true);
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() throws IOException {
        Collection<Object[]> data = new ArrayList<>();
        for(Map.Entry<String, Boolean> entry : directories.entrySet()) {
            File dir = new File(LanguageIT.class.getResource("/plang-strict/" + entry.getKey()).getFile());
            // this wouldn't work in jars, but we wouldn't want to run this in jars, would we?
            Path basePath = Paths.get(dir.toURI());
            Files.walk(basePath)
                .filter(Files::isRegularFile)
                .forEach(x -> {
                    data.add(new Object[]{
                        basePath.getParent().relativize(x).toString(), x.toFile(), entry.getValue()
                    });
                });
        }

        return data;
    }
}
