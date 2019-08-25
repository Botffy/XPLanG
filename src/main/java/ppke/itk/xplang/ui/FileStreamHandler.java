package ppke.itk.xplang.ui;

import ppke.itk.xplang.interpreter.ProgramInput;
import ppke.itk.xplang.common.StreamHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileStreamHandler implements StreamHandler {
    @Override
    public ProgramInput getStandardInput() {
        return new ProgramInput(new InputStreamReader(System.in, StandardCharsets.UTF_8), "StdIn");
    }

    @Override
    public Writer getStandardOutput() {
        return new BufferedWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8));
    }

    @Override
    public ProgramInput getFileInput(String name) throws FileNotFoundException {
        File file = new File(name);
        return new ProgramInput(
            new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8),
            file.getName()
        );
    }

    @Override
    public Writer getFileOutput(String name) throws FileNotFoundException {
        return new OutputStreamWriter(new FileOutputStream(new File(name)), StandardCharsets.UTF_8);
    }
}
