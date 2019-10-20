package ppke.itk.xplang.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.Root;
import ppke.itk.xplang.common.CompilerMessage;
import ppke.itk.xplang.common.ErrorLog;
import ppke.itk.xplang.parser.operator.ExpressionParser;

import java.io.IOException;
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
            this.lexer = new ppke.itk.xplang.parser.PlangHuLexer(source);
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
        try {
            log.trace("Advance");
            Token token = actual();

            do {
                act = lexer.next();
            } while (act.symbol() == Symbol.COMMENT || act.symbol() == Symbol.WHITESPACE || act.symbol() == Symbol.EOL);

            if (act.symbol() == Symbol.LEXER_ERROR) {
                log.error("Lexer error: invalid token {}", actual());
                throw new LexerError(actual());
            }
            return token;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     *  Checks whether the symbol under the tape head is the expected symbol, then advances the head.
     *  @param  symbol The expected symbol
     *  @param  errorCode The error code to emit.
     *  @return The accepted token.
     *  @throws LexerError if an invalid token is under the head.
     *  @throws ParseError if the actual symbol under tape head does not match the expected one.
     */
    public Token accept(Symbol symbol, ErrorCode errorCode) throws ParseError {
        if(!act.symbol().equals(symbol)) {
            log.error("Expected symbol {}, found {} ('{}')", symbol.getName(), act.symbol().getName(), act.lexeme());
            throw new ParseError(act.location(), errorCode, symbol, act.symbol());
        }

        Token token = actual();
        log.trace("Accepted symbol {} as {}", token.symbol(), token.lexeme());
        advance();
        return token;
    }

    /**
     *  Checks whether the symbol under the tape head is the expected symbol, then advances the head.
     *  @param  symbol The expected symbol
     *  @return The accepted token.
     *  @throws LexerError if an invalid token is under the head.
     *  @throws ParseError if the actual symbol under tape head does not match the expected one.
     */
    public Token accept(Symbol symbol) throws ParseError {
        return accept(symbol, ErrorCode.UNEXPECTED_SYMBOL);
    }

    public Expression parseExpression() throws ParseError {
        ExpressionParser expressionParser = new ExpressionParser(this);
        return expressionParser.parse();
    }

    public void skipToNextLine() throws LexerError {
        try {
            lexer.skipToNextLine();
            advance();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void recordError(CompilerMessage errorMessage) {
        errorLog.add(errorMessage);
    }

    public ErrorLog getErrorLog() {
        return errorLog;
    }
}
