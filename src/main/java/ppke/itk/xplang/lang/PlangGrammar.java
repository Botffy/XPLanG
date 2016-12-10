package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.*;
import ppke.itk.xplang.common.Translator;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.type.Scalar;
import ppke.itk.xplang.type.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class PlangGrammar extends Grammar {
    private final static Translator translator = Translator.getInstance("Plang");
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");

    public PlangGrammar() {
        super(Attribute.CASE_INSENSITIVE);
    }

    @Override
    public void setup(Context ctx) {
        log.debug("Setting up context");
        Stream.of(
            createSymbol("PROGRAM")    .matchingLiteral("program"),
            createSymbol("END_PROGRAM").matchingLiteral("program_vége"),
            createSymbol("DECLARE")    .matchingLiteral("változók"),
            createSymbol("ASSIGNMENT") .matchingLiteral(":="),
            createSymbol("COLON")      .matchingLiteral(":"),
            createSymbol("COMMA")      .matchingLiteral(","),
            createSymbol("IDENTIFIER")
                .matching("[a-zA-Záéíóöőúüű][a-zA-Z0-9_áéíóöőúüű]*")
                .withPrecedence(Symbol.Precedence.IDENTIFIER),
            createSymbol("LITERAL_INT")
                .withPrecedence(Symbol.Precedence.LITERAL)
                .matching("\\d+"),
            createSymbol("EOL")
                .matching(Symbol.EOLPattern)
                .notSignificant(),
            createSymbol("WHITESPACE")
                .matching("\\s+")
                .notSignificant(),
            createSymbol("COMMENT")
                .matching("\\*\\*[^\\r\\n]*")
                .notSignificant()
        ).forEach(x->x.register(ctx));

        try {
            ctx.declareType("Egész", new Scalar("IntegerType"));
            ctx.declareType("Logikai", new Scalar("BooleanType"));
        } catch(NameClashError nameClashError) {
            throw new RuntimeException("Failed to initialise PlangGrammar", nameClashError);
        }
    }

    /**
     * {@code start = Program}
     */
    @Override protected Root start(Parser parser) throws ParseError {
        log.debug("start");
        Program program = program(parser);
        return new Root(program);
    }

    /**
     * {@code Program = PROGRAM IDENTIFIER [Declarations] {Statement} END_PROGRAM}
     */
    protected Program program(Parser parser) throws ParseError {
        log.debug("Program");
        parser.accept("PROGRAM",
            translator.translate("plang.program_keyword_missing", "PROGRAM"));
        Token nameToken = parser.accept("IDENTIFIER",
            translator.translate("plang.missing_program_name"));

        if(parser.actual().symbol().equals(parser.context().lookup("DECLARE"))) {
            declarations(parser);
        }

        List<Statement> statementList = new ArrayList<>();
        List<Symbol> stoppers = Arrays.asList(parser.context().lookup("END_PROGRAM"), Symbol.EOF);
        do {
            try {
                statementList.add(statement(parser));
            } catch(ParseError error) {
                log.error("Parse error: ", error);
                parser.recordError(error.toErrorMessage());
                parser.advance();
                if(parser.actual().symbol().equals(Symbol.EOF)) break;
            }
        } while(!stoppers.contains(parser.actual().symbol()));

        parser.accept("END_PROGRAM",
            translator.translate("plang.missing_end_program", "PROGRAM_VÉGE"));
        Scope scope = parser.context().closeScope();
        Sequence sequence = new Sequence(statementList);

        return new Program(nameToken.lexeme(), new Block(scope, sequence));
    }

    /**
     * {@code Declarations = DECLARE COLON {variableDeclaration} [{COMMA variableDeclaration}}
     */
    protected void declarations(Parser parser) throws ParseError {
        log.debug("Declarations");
        parser.accept("DECLARE",
            translator.translate("plang.missing_declarations_keyword", "VÁLTOZÓK"));
        parser.accept("COLON",
            translator.translate("plang.missing_colon_after_declarations_keyword", "VÁLTOZÓK"));

        variableDeclaration(parser);
        while(parser.actual().symbol().equals(parser.context().lookup("COMMA"))) {
            parser.advance();
            variableDeclaration(parser);
        }
    }

    /**
     * {@code VariableDeclaration = IDENTIFIER [{COMMA IDENTIFIER}] COLON Typename}
     */
    protected void variableDeclaration(Parser parser) throws ParseError {
        log.debug("VariableDeclaration");
        List<Token> variables = new ArrayList<>();

        variables.add(parser.accept("IDENTIFIER"));
        while(parser.actual().symbol().equals(parser.context().lookup("COMMA"))) {
            parser.advance(); // consume the comma
            variables.add(parser.accept("IDENTIFIER"));
        }
        parser.accept("COLON");
        typename(parser);

        for(Token name : variables) {
            try {
                parser.context().declareVariable(name);
            } catch(NameClashError error) {
                parser.recordError(error.toErrorMessage());
            }
        }
    }

    /**
     *  {@code Typename = Identifier}
     */
    private Type typename(Parser parser) throws ParseError{
        log.debug("Typename");
        Token name = parser.accept("IDENTIFIER");
        return parser.context().lookupType(name);
    }

    /**
     * {@code Statement = Assignment}
     */
    protected Statement statement(Parser parser) throws ParseError {
        log.debug("Statement");
        Symbol act = parser.actual().symbol();
        if(act.equals(parser.context().lookup("IDENTIFIER"))) {
            return assignment(parser);
        }

        throw new SyntaxError(
            parser.context().lookup("IDENTIFIER"), act, parser.actual()
        );
    }

    /**
     * {@code Assignment = IDENTIFIER ASSIGNMENT RValue}
     */
    private Assignment assignment(Parser parser) throws LexerError, SyntaxError, NameError {
        log.debug("Assignment");
        Token var = parser.accept("IDENTIFIER");
        parser.accept("ASSIGNMENT");
        RValue rhs = rValue(parser);
        return new Assignment(
            parser.context().getVariableReference(var),
            rhs
        );
    }

    /**
     * {@code RValue = IDENTIFIER | LITERAL_INT}
     */
    private RValue rValue(Parser parser) throws LexerError, SyntaxError, NameError {
        log.debug("RValue");
        Symbol act = parser.actual().symbol();
        if(act.equals(parser.context().lookup("LITERAL_INT"))) {
            Token token = parser.accept("LITERAL_INT");
            return new IntegerLiteral(Integer.valueOf(token.lexeme()));
        } else if(act.equals(parser.context().lookup("IDENTIFIER"))) {
            Token namTok = parser.accept("IDENTIFIER");
            return parser.context().getVariableValue(namTok);
        }
        throw new SyntaxError(
            Arrays.asList(
                parser.context().lookup("IDENTIFIER"),
                parser.context().lookup("LITERAL_INT")
            ), act, parser.actual()
        );
    }
}
