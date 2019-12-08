package ppke.itk.xplang.parser;

/**
 * The symbols of the language.
 */
public enum Symbol {
    PROGRAM,
    END_PROGRAM,
    FUNCTION,
    END_FUNCTION,
    PROCEDURE,
    END_PROCEDURE,
    FORWARD_DECLARATION,
    CONSTANT,
    RECORD,
    END_RECORD,
    DECLARE,
    PRECONDITION,
    POSTCONDITION,
    IF,
    THEN,
    ELSIF,
    ELSE,
    ENDIF,
    LOOP,
    WHILE,
    END_LOOP,
    ASSERT,
    ASSIGNMENT,
    IN,
    OUT,
    OPEN,
    CLOSE,
    COLON,
    COMMA,
    DOT,
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
    OPERATOR_OLD,
    IDENTIFIER,
    LITERAL_INT,
    LITERAL_REAL,
    LITERAL_TRUE,
    LITERAL_FALSE,
    LITERAL_CHAR,
    LITERAL_STRING,
    EOL,
    EOF,
    WHITESPACE,
    COMMENT,
    LEXER_ERROR;

    public String getName() {
        return name();
    }
}
