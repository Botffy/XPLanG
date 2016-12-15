package ppke.itk.xplang.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.Root;
import ppke.itk.xplang.common.CompilerMessage;
import ppke.itk.xplang.common.ErrorLog;

import java.io.Reader;

public class Parser {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser");

    private final ErrorLog errorLog;
    private Context context;
    private Lexer lexer;
    private Token act;

    public Parser() {
        this(new Context(), new ErrorLog());
    }

    public Parser(ErrorLog errorLog) {
        this(new Context(), errorLog);
    }

    public Parser(Context context) {
        this(context, new ErrorLog());
    }

    public Parser(Context context, ErrorLog errorLog) {
        this.context = context;
        this.errorLog = errorLog;
    }

    /**
     * Parse the given source text, using the given grammar.
     * @param source The reader pointing at the source text to be parsed.
     * @param grammar The grammar used during parsing.
     * @return The root node of the generated {@link ppke.itk.xplang.ast AST}, or null if the parsing failed.
     */
    public Root parse(Reader source, Grammar grammar) {
        try {
            grammar.setup(context);
            this.lexer = new Lexer(source, context.getSymbols());
            advance();
            return grammar.start(this);
        } catch(ParseError e) {
            recordError(e.toErrorMessage());
            return null;
        }
    }

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
    public Token advance() throws LexerError {
        log.trace("Advance");
        Token token = actual();
        act = lexer.next();

        if(act.symbol().equals(Symbol.LEXER_ERROR)) {
            log.error("Lexer error: invalid token {}", actual());
            throw new LexerError(actual());
        }
        return token;
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
        if(!act.symbol().equals(symbol)) {
            log.error("Expected symbol {}, found {} ('{}')", symbol.getName(), act.symbol().getName(), act.lexeme());
            if(message != null) {
                throw new SyntaxError(message, symbol, act.symbol(), act);
            } else {
                throw new SyntaxError(symbol, act.symbol(), act);
            }
        }

        Token token = actual();
        log.trace("Accepted symbol {} as {}", token.symbol(), token.lexeme());
        advance();
        return token;
    }

    public Token accept(Symbol symbol) throws LexerError, SyntaxError {
        return accept(symbol, null);
    }

    public void skipToNextLine() throws LexerError {
        lexer.skipToNextLine();
        advance();
    }

    public Symbol symbol(Enum name) {
        return context.lookup(name.name());
    }

    public void recordError(CompilerMessage errorMessage) {
        errorLog.add(errorMessage);
    }

    public ErrorLog getErrorLog() {
        return errorLog;
    }
}
