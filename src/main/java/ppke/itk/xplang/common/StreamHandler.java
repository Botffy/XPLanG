package ppke.itk.xplang.common;

import java.io.*;

public interface StreamHandler {
    Reader getStandardInput();
    Writer getStandardOutput();
    Reader getFileInput(String name) throws FileNotFoundException;
    Writer getFileOutput(String name) throws FileNotFoundException;
}
