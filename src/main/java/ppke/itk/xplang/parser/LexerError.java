package ppke.itk.xplang.parser;

public class LexerError extends ParseError {
    LexerError(Token token) {
        super(String.format("Encountered invalid token {}", token), token.getLine(), token.getCol());
    }
}
