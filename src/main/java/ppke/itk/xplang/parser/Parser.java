package ppke.itk.xplang.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;

public class Parser {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser");

    private Context context;
    private Lexer lexer;

    public Parser(Reader source, Context context) throws LexerError {
        this.context = context;
        this.lexer = new Lexer(source, context.getSymbols());
        advance();
    }

    private Token act;

    /**
     * Get the Parser context.
     */
    public Context context() {
        return this.context;
    }

    /**
     *  The symbol under the tape head.
     *  @return The token under the tape head;
     */
    public Token actual() {
        return act;
    }

    /**
     *  Advances the tape head.
     *  @throws  LexerError if meets an unrecognized token.
     */
    public void advance() throws LexerError {
        log.trace("Advance");
        act = lexer.next();

        if(act.getSymbol().equals(Symbol.LEXER_ERROR)) {
            log.error("Lexer error: invalid token {}", actual());
            throw new LexerError(actual());
        }
    }

    /**
     *  Checks whether the symbol under the tape head is the expected symbol, then advances the head.
     *  @param  symbol The expected symbol
     *  @param  message The message the exception should contain if the actual symbol does not match the expected one.
     *  @return The accepted token.
     *  @throws LexerError if an invalid token is under the head.
     *  @throws SyntaxError if the actual symbol under tape head does not match the expected one.
     */
    public Token accept(Symbol symbol, String message) throws LexerError, SyntaxError {
        if(!act.getSymbol().equals(symbol)) {
            log.error(message, symbol, act.getSymbol());
            if(message != null) {
                throw new SyntaxError(message, symbol, act.getSymbol(), act);
            } else {
                throw new SyntaxError(symbol, act.getSymbol(), act);
            }
        }

        Token token = actual();
        log.trace("Accepted symbol {} as {}", token.getSymbol(), token.getLexeme());
        advance();
        return token;
    }

    /**
     * Convenient variant of {@link #accept(Symbol, String)}.
     * @param symbolName name of the expected symbol.
     * @param message The message the exception should contain if the actual symbol does not match the expected one.
     * @return the accepted token.
     * @throws LexerError if an invalid token is under the head.
     * @throws SyntaxError if the actual symbol under tape head does not match the expected one.
     */
    public Token accept(String symbolName, String message) throws LexerError, SyntaxError {
        return accept(context.getSymbol(symbolName), message);
    }
}
