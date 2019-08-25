package ppke.itk.xplang.common;

import ppke.itk.xplang.interpreter.ProgramInput;

import java.io.*;

public interface StreamHandler {
    ProgramInput getStandardInput();
    Writer getStandardOutput();
    ProgramInput getFileInput(String name) throws FileNotFoundException;
    Writer getFileOutput(String name) throws FileNotFoundException;
}
