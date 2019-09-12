package ppke.itk.xplang.gui;

import ppke.itk.xplang.common.StreamHandler;
import ppke.itk.xplang.interpreter.ProgramInput;

import java.io.*;
import java.nio.charset.StandardCharsets;

class GuiStreamHandler implements StreamHandler {
    private final PipedOutputStream stdOut;

    GuiStreamHandler(PipedOutputStream stdOut) {
        this.stdOut = stdOut;
    }

    public void close() {
        try {
            stdOut.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public ProgramInput getStandardInput() {
        return null;
    }

    @Override
    public Writer getStandardOutput() {
        return new BufferedWriter(new OutputStreamWriter(stdOut, StandardCharsets.UTF_8));
    }

    @Override
    public ProgramInput getFileInput(String name) throws FileNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Writer getFileOutput(String name) throws FileNotFoundException {
        throw new UnsupportedOperationException();
    }
}