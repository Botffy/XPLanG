package language;

import ppke.itk.xplang.common.StreamHandler;
import ppke.itk.xplang.interpreter.ProgramInput;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class CorpusTestStreamHandler implements StreamHandler {
    private ByteArrayInputStream stdIn;
    private ByteArrayOutputStream stdOut = new ByteArrayOutputStream();

    public CorpusTestStreamHandler(String stdIn) {
        this.stdIn = new ByteArrayInputStream(stdIn.getBytes(StandardCharsets.UTF_8));
    }

    public String getStdOut() {
        return new String(stdOut.toByteArray(), StandardCharsets.UTF_8);
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
        if (name.equalsIgnoreCase("BEMENET")) {
            return new ProgramInput(new InputStreamReader(stdIn), "stdIn");
        }
        throw new FileNotFoundException("AC tests has no file inputs.");
    }

    @Override
    public Writer getFileOutput(String name) throws FileNotFoundException {
        throw new FileNotFoundException("AC tests has no file outputs.");
    }

}
