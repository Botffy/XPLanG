package ppke.itk.xplang.interpreter;

interface InputStreamValue extends Value {
    int readInt();
    double readReal();
    String readLine();
    char readCharacter();
    boolean readBoolean();
}
