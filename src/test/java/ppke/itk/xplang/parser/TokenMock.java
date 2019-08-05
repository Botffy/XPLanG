package ppke.itk.xplang.parser;

import ppke.itk.xplang.common.Location;

public class TokenMock {
    public static Token token(Symbol symbol, String lexeme, Location location) {
        return new Token(symbol, lexeme, location);
    }

    public static Token token(Symbol symbol, String lexeme) {
        return new Token(symbol, lexeme, Location.NONE);
    }
}
