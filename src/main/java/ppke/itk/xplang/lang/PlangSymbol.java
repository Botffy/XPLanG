package ppke.itk.xplang.lang;

import ppke.itk.xplang.parser.Symbol;

/**
 * The symbols of the language.
 */
enum PlangSymbol {
    PROGRAM(true, "program"),
    END_PROGRAM(true, "program_vége"),
    DECLARE(true, "változók"),
    IF(true, "ha"),
    THEN(true, "akkor"),
    ELSE(true, "különben"),
    ENDIF(true, "ha_vége"),
    ASSIGNMENT(true, ":="),
    COLON(true, ":"),
    COMMA(true, ","),
    BRACKET_OPEN(true, "["),
    BRACKET_CLOSE(true, "]"),
    IDENTIFIER(false, "[a-zA-Záéíóöőúüű][a-zA-Z0-9_áéíóöőúüű]*"),
    LITERAL_INT(false, "\\d+"),
    LITERAL_REAL(false, "\\d\\.\\d+"),
    LITERAL_BOOL(false, "(igaz)|(hamis)"),
    LITERAL_CHAR(false, "'\\S'"),
    LITERAL_STRING(false, "\"[^\"]*\""),
    EOL(false, Symbol.EOL_PATTERN),
    WHITESPACE(false, "\\s+"),
    COMMENT(false, "\\*\\*[^\\r\\n]*");

    private final boolean literal;
    private final String pattern;

    PlangSymbol(boolean literal, String pattern) {
        this.literal = literal;
        this.pattern = pattern;
    }

    public boolean isLiteral() {
        return literal;
    }

    public String getPattern() {
        return pattern;
    }
}
