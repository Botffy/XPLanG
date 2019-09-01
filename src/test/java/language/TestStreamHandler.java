package language;

import ppke.itk.xplang.ast.Output;
import ppke.itk.xplang.interpreter.ProgramInput;
import ppke.itk.xplang.common.StreamHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class TestStreamHandler implements StreamHandler {
    private ByteArrayInputStream stdIn;
    private ByteArrayOutputStream stdOut = new ByteArrayOutputStream();

    Map<String, ByteArrayInputStream> inFiles = new HashMap<>();
    Map<String, ByteArrayOutputStream> outFiles = new HashMap<>();

    public TestStreamHandler(String stdIn, Map<String, String> inFiles) {
        this.stdIn = new ByteArrayInputStream(stdIn.getBytes(StandardCharsets.UTF_8));
        this.inFiles = inFiles.entrySet().stream()
            .collect(toMap(
                Map.Entry::getKey,
                x -> new ByteArrayInputStream(x.getValue().getBytes(StandardCharsets.UTF_8))
            ));
    }

    public String getStdOut() {
        return new String(stdOut.toByteArray(), StandardCharsets.UTF_8);
    }

    public String getOutFile(String name) throws FileNotFoundException {
        if (!outFiles.containsKey(name)) {
            throw new FileNotFoundException("Nothing written to file " + name);
        }

        return new String(outFiles.get(name).toByteArray(), StandardCharsets.UTF_8);
    }

    @Override
    public ProgramInput getStandardInput() {
        return new ProgramInput(new InputStreamReader(stdIn), "stdIn");
    }

    @Override
    public Writer getStandardOutput() {
        return new OutputStreamWriter(stdOut);
    }

    @Override
    public ProgramInput getFileInput(String name) throws FileNotFoundException {
        InputStream stream = inFiles.get(name);
        if (stream == null) {
            throw new FileNotFoundException("No test input called " + name);
        }

        return new ProgramInput(new InputStreamReader(stream, StandardCharsets.UTF_8), name);
    }

    @Override
    public Writer getFileOutput(String name) throws FileNotFoundException {
        if (!outFiles.containsKey(name)) {
            outFiles.put(name, new ByteArrayOutputStream());
        }

        return new PrintWriter(
            new BufferedWriter(new OutputStreamWriter(outFiles.get(name), StandardCharsets.UTF_8)),
            true
        );
    }
}
