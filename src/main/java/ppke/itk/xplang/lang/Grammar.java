package ppke.itk.xplang.lang;

import ppke.itk.xplang.ast.Root;
import ppke.itk.xplang.parser.Context;
import ppke.itk.xplang.parser.Parser;
import ppke.itk.xplang.parser.Symbol;

import java.util.EnumSet;


/**
 *  Description of a language.
 */
abstract public class Grammar {

    public static enum Attribute {
        /**
         * Grammars are case-sensitive by default. This attribute makes them case-insensitive. Used in the
         */
        CASE_INSENSITIVE
    }

    private final EnumSet<Attribute> attributes;

    public Grammar() {
        this(EnumSet.noneOf(Attribute.class));
    }
    public Grammar(EnumSet<Attribute> attributes) {
        this.attributes = attributes;
    }

    /**
     *  Prelude to the grammar: registers terminal symbols, functions, global variables, etc.
     *  @param ctx The parsing context to be populated.
     */
    abstract public void setup(Context ctx);

    /**
     *  The starting symbol of the grammar. This starts the recursive descent.
     *  @param parser The parser object.
     */
    abstract public Root S(Parser parser);

    protected Symbol.Builder createSymbol() {
        Symbol.Builder builder = Symbol.create();
        if(attributes.contains(Attribute.CASE_INSENSITIVE)) {
            builder.caseInsensitive();
        }
        return builder;
    }
}
