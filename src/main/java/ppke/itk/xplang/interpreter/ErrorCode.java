package ppke.itk.xplang.interpreter;

public enum ErrorCode {
    /** The interpreter has been stopped from the outside. */
    INTERPRETER_STOPPED,

    /** The execution of the program has taken more steps than the maximum allowed number of steps. */
    EXECUTION_LIMIT_REACHED,

    /** An assertion has been violated the program. */
    ASSERTION_FAILURE,

    /** The program tried to read from an open input, but failed. Indicates a type error. */
    FAILED_TO_READ_FROM_INPUT,

    /** The program tried to read from or write to an unopened stream. */
    STREAM_NOT_OPEN,

    /** The program tried to open a file for reading or writing, but failed. */
    FAILED_TO_OPEN_FILE,

    /** Array index was out of bounds. */
    ILLEGAL_INDEX,

    /** When performing a SLICE operation, the start index was out of bounds. */
    ILLEGAL_START_INDEX,

    /** When performing a SLICE operation, the end index was out of bounds. */
    ILLEGAL_END_INDEX,

    /** Tried to use a null value. */
    NULL_ERROR,
}
