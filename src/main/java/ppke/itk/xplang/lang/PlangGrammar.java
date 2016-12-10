package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.*;
import ppke.itk.xplang.common.Translator;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.type.Scalar;
import ppke.itk.xplang.type.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

public class PlangGrammar extends Grammar {
    private final static Translator translator = Translator.getInstance("Plang");
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");

    public PlangGrammar() {
        super(Attribute.CASE_INSENSITIVE);
    }

    @Override
    public void setup(Context ctx) {
        log.debug("Setting up context");
        try {
            Stream.of(
                createSymbol("PROGRAM")    .matchingLiteral("program"),
                createSymbol("END_PROGRAM").matchingLiteral("program_vége"),
                createSymbol("DECLARE")    .matchingLiteral("változók"),
                createSymbol("IF")         .matchingLiteral("HA"),
                createSymbol("THEN")       .matchingLiteral("AKKOR"),
                createSymbol("ENDIF")      .matchingLiteral("HA_VÉGE"),
                createSymbol("ASSIGNMENT") .matchingLiteral(":="),
                createSymbol("COLON")      .matchingLiteral(":"),
                createSymbol("COMMA")      .matchingLiteral(","),
                createSymbol("IDENTIFIER")
                    .matching("[a-zA-Záéíóöőúüű][a-zA-Z0-9_áéíóöőúüű]*")
                    .withPrecedence(Symbol.Precedence.IDENTIFIER),
                createSymbol("LITERAL_INT")
                    .withPrecedence(Symbol.Precedence.LITERAL)
                    .matching("\\d+"),
                createSymbol("LITERAL_BOOL")
                    .withPrecedence(Symbol.Precedence.LITERAL)
                    .matching("(igaz)|(hamis)"),
                createSymbol("EOL")
                    .matching(Symbol.EOLPattern)
                    .notSignificant(),
                createSymbol("WHITESPACE")
                    .matching("\\s+")
                    .notSignificant(),
                createSymbol("COMMENT")
                    .matching("\\*\\*[^\\r\\n]*")
                    .notSignificant()
            ).forEach(x -> x.register(ctx));

            ctx.declareType("Egész", new Scalar("IntegerType"));
            ctx.declareType("Logikai", new Scalar("BooleanType"));
        } catch(ParseError error) {
            throw new IllegalStateException("Failed to initialise PlangGrammar", error);
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
     * {@code Program = PROGRAM IDENTIFIER [Declarations] Sequence END_PROGRAM}
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

        Sequence sequence = sequence(parser, parser.context().lookup("END_PROGRAM"));

        parser.accept("END_PROGRAM",
            translator.translate("plang.missing_end_program", "PROGRAM_VÉGE"));
        Scope scope = parser.context().closeScope();

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
        Type type = typename(parser);

        for(Token name : variables) {
            try {
                parser.context().declareVariable(name, type);
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
     * {@code Sequence = { Statement }}
     */
    private Sequence sequence(Parser parser, Symbol stopSymbol) throws LexerError {
        log.debug("Sequence");
        List<Statement> statementList = new ArrayList<>();
        List<Symbol> stoppers = asList(stopSymbol, Symbol.EOF);
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

        return new Sequence(statementList);
    }

    /**
     * {@code Statement = Assignment}
     */
    protected Statement statement(Parser parser) throws ParseError {
        log.debug("Statement");
        Symbol act = parser.actual().symbol();
        if(act.equals(parser.context().lookup("IDENTIFIER"))) {
            return assignment(parser);
        } else if(act.equals(parser.context().lookup("IF"))) {
            return conditional(parser);
        }

        throw new SyntaxError(asList(
            parser.context().lookup("IDENTIFIER"),
            parser.context().lookup("IF")
        ), act, parser.actual());
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
     * {@code Conditional = IF RValue THEN {Statement} ENDIF}
     */
    private Statement conditional(Parser parser) throws ParseError {
        log.debug("Conditional");
        parser.accept(parser.context().lookup("IF"));
        rValue(parser);
        parser.accept(parser.context().lookup("THEN"));
        sequence(parser, parser.context().lookup("ENDIF"));
        parser.accept(parser.context().lookup("ENDIF"));
        return new Conditional();
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
        } else if(act.equals(parser.context().lookup("LITERAL_BOOL"))) {
            Token token = parser.accept("LITERAL_BOOL");
            // FIXME, but this should be taken care of by operators and expressions
            return new BooleanLiteral(token.lexeme().equalsIgnoreCase("igaz")? true : false);
        } else if(act.equals(parser.context().lookup("IDENTIFIER"))) {
            Token namTok = parser.accept("IDENTIFIER");
            return parser.context().getVariableValue(namTok);
        }
        throw new SyntaxError(
            asList(
                parser.context().lookup("IDENTIFIER"),
                parser.context().lookup("LITERAL_INT")
            ), act, parser.actual()
        );
    }
}
