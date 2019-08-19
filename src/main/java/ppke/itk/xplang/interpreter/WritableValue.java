package ppke.itk.xplang.interpreter;

import java.io.IOException;
import java.io.Writer;

public interface WritableValue extends Value {
    void writeTo(Writer writer) throws IOException;
}
