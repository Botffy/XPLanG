package ppke.itk.xplang.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.Reader;

public class Parser {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser");

    private Context context;
    private Lexer lexer;

    public Parser(Reader source, Context context) throws LexerError {
        this.context = new Context();
        this.lexer = new Lexer(source, context.getSymbols());
        advance();
    }

    private Token act;

    /**
     *  The symbol under the tape head.
     *
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
     *  @return The accepted token.
     *  @throws LexerError if an invalid token is under the head.
     *  @throws SyntaxError if the actual symbol under tape head does not match the expected one.
     */
    public Token accept(Symbol symbol) throws LexerError, SyntaxError {
        if(!act.getSymbol().equals(symbol)) {
            log.error("Syntax error: expected {}, got {}", symbol, act.getSymbol());
            throw new SyntaxError(symbol, act.getSymbol(), act);
        }

        Token token = actual();
        log.trace("Accepted symbol {} as {}", token.getSymbol(), token.getLexeme());
        advance();
        return token;
    }
}
