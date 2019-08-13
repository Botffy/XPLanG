package ppke.itk.xplang.parser;

import ppke.itk.xplang.ast.Root;
import ppke.itk.xplang.type.Scalar;


/**
 *  Description of a language.
 */
abstract public class Grammar {
    /**
     *  Prelude to the grammar: registers terminal symbols, functions, global variables, etc.
     *  @param ctx The parsing context to be populated.
     */
    abstract protected void setup(Context ctx);

    /**
     *  The starting symbol of the grammar. This starts the recursive descent.
     *  @param parser The parser object.
     */
    abstract protected Root start(Parser parser) throws ParseError;
}
