package ppke.itk.xplang.parser;

import java.io.IOException;

public interface Lexer {
    Token next() throws IOException;
    void skipToNext(Symbol symbol) throws IOException;
}
