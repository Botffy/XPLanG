package ppke.itk.xplang.interpreter;

public interface ReadableValue extends Value {
    Value readFrom(InputStreamValue input);
}
