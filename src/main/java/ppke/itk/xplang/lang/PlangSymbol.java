package ppke.itk.xplang.lang;

import ppke.itk.xplang.parser.Symbol;

/**
 * The symbols of the language.
 */
enum PlangSymbol {
    PROGRAM(matchingLiteral("program")),
    END_PROGRAM(matchingLiteral("program_vége")),
    DECLARE(matchingLiteral("változók")),
    IF(matchingLiteral("ha")),
    THEN(matchingLiteral("akkor")),
    ELSE(matchingLiteral("különben")),
    ENDIF(matchingLiteral("ha_vége")),
    ASSIGNMENT(matchingLiteral(":=")),
    COLON(matchingLiteral(":")),
    COMMA(matchingLiteral(",")),
    BRACKET_OPEN(matchingLiteral("[")),
    BRACKET_CLOSE(matchingLiteral("]")),
    IDENTIFIER(matching("[a-zA-Záéíóöőúüű][a-zA-Z0-9_áéíóöőúüű]*").withPrecedence(Symbol.Precedence.IDENTIFIER)),
    LITERAL_INT(matching("\\d+").withPrecedence(Symbol.Precedence.LITERAL)),
    LITERAL_BOOL(matching("(igaz)|(hamis)").withPrecedence(Symbol.Precedence.LITERAL)),
    EOL(matching(Symbol.EOL_PATTERN).notSignificant()),
    WHITESPACE(matching("\\s+").notSignificant()),
    COMMENT(matching("\\*\\*[^\\r\\n]*").notSignificant());

    private final Symbol symbol;

    private static Symbol.Builder matching(String pattern) {
        return Symbol.create().matching(pattern);
    }

    private static Symbol.Builder matchingLiteral(String literal) {
        return Symbol.create().matchingLiteral(literal);
    }

    PlangSymbol(Symbol.Builder symbolBuilder) {
        this.symbol = symbolBuilder
            .caseInsensitive()
            .named(this.name())
            .build();
    }

    public Symbol symbol() {
        return symbol;
    }
}
