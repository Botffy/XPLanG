package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.parser.ParseError;
import ppke.itk.xplang.parser.Parser;
import ppke.itk.xplang.parser.Symbol;
import ppke.itk.xplang.parser.Token;
import ppke.itk.xplang.type.RecordType;
import ppke.itk.xplang.type.Type;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.empty;

/**
 * {@code Record = RECORD IDENTIFIER { FieldDeclaration [COMMA] } END_RECORD }
 */
class RecordParser {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");


    public static Type parse(Parser parser) throws ParseError {
        parser.accept(Symbol.RECORD);
        Token nameToken = parser.accept(Symbol.IDENTIFIER);
        PlangName name = new TypeName(nameToken.lexeme());
        List<RecordType.Field> fields = parseFields(parser).collect(toList());
        RecordType result = new RecordType(name.toString(), fields);
        parser.accept(Symbol.END_RECORD);
        return result;
    }

    private static Stream<RecordType.Field> parseFields(Parser parser) throws ParseError {
        Stream<RecordType.Field> fields = empty();

        do {
            fields = concat(fields, parseFieldLine(parser));
            if (parser.actual().symbol() == Symbol.COMMA) {
                parser.advance();
            }
        } while (parser.actual().symbol() != Symbol.END_RECORD);

        return fields;
    }

    private static Stream<RecordType.Field> parseFieldLine(Parser parser) throws ParseError {
        Stream<Token> fields = Stream.of(parser.accept(Symbol.IDENTIFIER));
        while(parser.actual().symbol() == Symbol.COMMA) {
            parser.advance();
            fields = concat(fields, Stream.of(parser.accept(Symbol.IDENTIFIER)));
        }
        parser.accept(Symbol.COLON);
        Type type = TypenameParser.parse(parser);

        return fields
            .map(Token::lexeme)
            .map(PlangName::new)
            .map(x -> new RecordType.Field(x, type));
    }
}
