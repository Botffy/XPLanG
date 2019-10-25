package ppke.itk.xplang.interpreter;

import ppke.itk.xplang.common.StreamHandler;

import java.io.FileNotFoundException;
import java.io.Writer;

public class MockStreamHandler implements StreamHandler {
    @Override
    public ProgramInput getStandardInput() {
        return null;
    }

    @Override
    public Writer getStandardOutput() {
        return null;
    }

    @Override
    public ProgramInput getFileInput(String name) throws FileNotFoundException {
        return null;
    }

    @Override
    public Writer getFileOutput(String name) throws FileNotFoundException {
        return null;
    }
}
