package ppke.itk.xplang.lang;

/**
 * The symbols of the language.
 */
enum PlangSymbol {
    PROGRAM,
    END_PROGRAM,
    DECLARE,
    IF,
    THEN,
    ELSE,
    ENDIF,
    LOOP,
    WHILE,
    END_LOOP,
    ASSIGNMENT,
    IN,
    OUT,
    OPEN,
    CLOSE,
    COLON,
    COMMA,
    PAREN_OPEN,
    PAREN_CLOSE,
    BRACKET_OPEN,
    BRACKET_CLOSE,
    OPERATOR_NOT,
    OPERATOR_OR,
    OPERATOR_AND,
    OPERATOR_MINUS,
    OPERATOR_PLUS,
    OPERATOR_TIMES,
    OPERATOR_DIV,
    OPERATOR_IDIV,
    OPERATOR_IMOD,
    OPERATOR_EXP,
    OPERATOR_PIPE,
    OPERATOR_EQ,
    OPERATOR_NEQ,
    OPERATOR_LT,
    OPERATOR_LTE,
    OPERATOR_GT,
    OPERATOR_GTE,
    OPERATOR_FIND,
    IDENTIFIER,
    LITERAL_INT,
    LITERAL_REAL,
    LITERAL_BOOL,
    LITERAL_CHAR,
    LITERAL_STRING,
    EOL,
    WHITESPACE,
    COMMENT
}
