package ppke.itk.xplang.lang;

import ppke.itk.xplang.parser.Context;
import ppke.itk.xplang.parser.Parser;
import ppke.itk.xplang.parser.Symbol;

import java.util.EnumSet;


/**
 *  Description of a language.
 */
abstract public class Grammar {
    /**
     *  Prelude to the grammar: registers terminal symbols, functions, global variables, etc.
     *  @param ctx The parsing context to be populated.
     */
    abstract public void setup(Context ctx);

    /**
     *  The starting symbol of the grammar. This starts the recursive descent.
     *  @param parser The parser object.
     */
    abstract public void S(Parser parser);

}
