package ppke.itk.xplang.ui;

import ppke.itk.xplang.common.StreamHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileStreamHandler implements StreamHandler {
    @Override
    public Reader getStandardInput() {
        return new InputStreamReader(System.in, StandardCharsets.UTF_8);
    }

    @Override
    public Writer getStandardOutput() {
        return new BufferedWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8));
    }

    @Override
    public Reader getFileInput(String name) throws FileNotFoundException {
        return new InputStreamReader(new FileInputStream(new File(name)), StandardCharsets.UTF_8);
    }

    @Override
    public Writer getFileOutput(String name) throws FileNotFoundException {
        return new OutputStreamWriter(new FileOutputStream(new File(name)), StandardCharsets.UTF_8);
    }
}
