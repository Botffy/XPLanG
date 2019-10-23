package ppke.itk.xplang.parser;

import java.io.IOException;
import java.util.Set;

public interface Lexer {
    Token next() throws IOException;
    Token skipToNext(Set<Symbol> symbol) throws IOException;
}
