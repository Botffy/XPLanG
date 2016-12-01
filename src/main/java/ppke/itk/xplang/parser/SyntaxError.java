package ppke.itk.xplang.parser;

public class SyntaxError extends ParseError {
    public SyntaxError(Symbol expected, Symbol actual, Token token) {
        super(String.format("Expected symbol %s, encountered %s", expected, actual), token.getLine(), token.getCol());
    }

    public SyntaxError(String message, Symbol expected, Symbol actual, Token token) {
        super(String.format(message, expected, actual), token.getLine(), token.getCol());
    }
}

