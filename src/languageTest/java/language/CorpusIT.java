package language;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ppke.itk.xplang.ast.Root;
import ppke.itk.xplang.common.CompilerMessage;
import ppke.itk.xplang.common.ErrorLog;
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
import java.util.List;

import static org.junit.Assume.assumeTrue;

/**
 * Tests the interpreter against the corpus of the programming assignments of 2016, 2017 and 2018.
 */
@RunWith(Parameterized.class)
public class CorpusIT {
    private static final Grammar grammar = new PlangGrammar();

    private static Path basePath;

    static {
        String location = System.getProperty("plang.corpus.directory");
        File dir = new File(location);
        basePath = Paths.get(dir.toURI());
        assumeTrue(dir.exists() && dir.isDirectory());
    }


    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() throws IOException {
        Collection<Object[]> data = new ArrayList<>();
        Files.walk(basePath)
            .filter(Files::isRegularFile)
            .filter(x -> x.toString().endsWith(".plang"))
            .filter(CorpusIT::hasData)
            .map(CorpusIT::getData)
            .flatMap(Collection::stream)
            .forEach(data::add);

        return data;
    }

    private static boolean hasData(Path plang) {
        String fileName = plang.getFileName().toString();
        Path h = plang.resolveSibling(fileName + ".1.h");
        Path o = plang.resolveSibling(fileName + ".1.o");
        return h.toFile().exists() && o.toFile().exists();
    }

    private File source;
    private File h;
    private File o;
    private File input;

    public CorpusIT(File source, File h, File o, File input) {
        this.source = source;
        this.h = h;
        this.o = o;
        this.input = input;
    }

    private static List<Object[]> getData(Path path) {
        String fileName = path.getFileName().toString();
        String feladat = FilenameUtils.removeExtension(fileName);
        int i = 1;
        List<Object[]> result = new ArrayList<>();
        while (true) {
            File source = path.toFile();
            File h = path.resolveSibling(String.format("%s.%d.h", fileName, i)).toFile();
            File o = path.resolveSibling(String.format("%s.%d.o", fileName, i)).toFile();
            File input = basePath.resolve("feladatok").resolve(String.format("%s.%d.input", feladat, i)).toFile();
            if (!(h.exists() && o.exists() && input.exists())) {
                break;
            }

            result.add(new Object[]{source, h, o, input});

            ++i;
        }

        return result;
    }

    @Test
    public void test() throws Exception {
        String output = IOUtils.toString(new InputStreamReader(new FileInputStream(o), StandardCharsets.UTF_8)).trim();
        String hiba = IOUtils.toString(new InputStreamReader(new FileInputStream(h), StandardCharsets.UTF_8)).trim();
        String inputString = IOUtils.toString(new InputStreamReader(new FileInputStream(input), StandardCharsets.UTF_8));
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(source), StandardCharsets.ISO_8859_1))) {
            ErrorLog errorLog = new ErrorLog();
            Parser parser = new Parser(errorLog);
            Root root = parser.parse(reader, grammar);

            if (!errorLog.isEmpty()) {
                for(CompilerMessage message : errorLog.getErrorMessages()) {
                    System.out.println(message);
                }
                if (hiba.isEmpty()) {
                    Assert.fail("Failed parsing while original passed.");
                }
                return;
            }

            CorpusTestStreamHandler streamHandler = new CorpusTestStreamHandler(inputString);
            Interpreter interpreter = new Interpreter(streamHandler);
            try {
                interpreter.visit(root);
                System.out.println(output);
                System.out.println(streamHandler.getStdOut());
                Assert.assertEquals(output, streamHandler.getStdOut().trim());
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
                if (hiba.isEmpty()) {
                    e.printStackTrace();
                    Assert.fail("Failed interpreting while the original passed");
                }
            }
        }
    }
}
