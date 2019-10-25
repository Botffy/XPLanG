package ppke.itk.xplang.parser;

public enum ErrorCode {
    LEXER_ERROR,

    UNEXPECTED_SYMBOL,
    UNEXPECTED_SYMBOL_OF_ANY,
    EXPECTED_COLON_AFTER_VARIABLE,
    EXPECTED_ROOT_ELEMENT,
    EXPECTED_PROGRAM,
    EXPECTED_PROGRAM_NAME,
    EXPECTED_DECLARE,
    EXPECTED_END_PROGRAM,
    ARRAY_LENGTH_EXPECT_INTEGER_LITERAL,
    CONSTANT_CAN_ONLY_BE_SCALAR,

    NAME_ERROR,
    NAME_CLASH,
    NO_SUCH_TYPE,
    NOT_A_TYPE,
    NO_SUCH_VARIABLE,
    NO_SUCH_PREFIX_OP,
    NO_SUCH_INFIX_OP,

    TYPE_MISMATCH,
    TYPE_MISMATCH_ASSIGNMENT,
    TYPE_MISMATCH_OPEN_STREAM,
    TYPE_MISMATCH_CLOSE_STREAM,
    TYPE_MISMATCH_NOT_OUTPUTSTREAM,
    TYPE_MISMATCH_CONDITIONAL,
    TYPE_MISMATCH_NOT_ADDRESSABLE,
    TYPE_MISMATCH_ARRAY_INDEX,
    TYPE_MISMATCH_NOT_SLICABLE,
    TYPE_MISMATCH_SLICE_INDEX,
    TYPE_MISMATCH_NOT_RECORD,
    TYPE_MISMATCH_NO_SUCH_FIELD_IN_RECORD,
    TYPE_MISMATCH_ASSERTION,
    TYPE_MISMATCH_NOT_READABLE,

    FUNCTION_AMBIGUOUS,
    NO_VIABLE_FUNCTIONS,

    MISSING_ENTRY_POINT,
    ENTRY_POINT_ALREADY_DEFINED,
    FUNCTION_DECLARED_NOT_DEFINED
}
