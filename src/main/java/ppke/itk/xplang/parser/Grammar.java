package ppke.itk.xplang.parser;

import ppke.itk.xplang.ast.Root;

import java.util.EnumSet;


/**
 *  Description of a language.
 */
abstract public class Grammar {

    public static enum Attribute {
        /**
         * Grammars are case-sensitive by default. This attribute makes them case-insensitive. Used when compiling the
         * regex pattern of a {@link Symbol};
         */
        CASE_INSENSITIVE;
    }
    private final EnumSet<Attribute> attributes;

    public Grammar() {
        this(EnumSet.noneOf(Attribute.class));
    }

    public Grammar(Attribute caseInsensitive) {
        this(EnumSet.of(caseInsensitive));
    }

    public Grammar(EnumSet<Attribute> attributes) {
        this.attributes = attributes;
    }

    /**
     *  Prelude to the grammar: registers terminal symbols, functions, global variables, etc.
     *  @param ctx The parsing context to be populated.
     */
    abstract protected void setup(Context ctx);

    /**
     *  The starting symbol of the grammar. This starts the recursive descent.
     *  @param parser The parser object.
     */
    abstract protected Root S(Parser parser) throws ParseError;

    protected Symbol.Builder createSymbol() {
        Symbol.Builder builder = Symbol.create();
        if(attributes.contains(Attribute.CASE_INSENSITIVE)) {
            builder.caseInsensitive();
        }
        return builder;
    }

    protected Symbol.Builder createSymbol(String name) {
        return this.createSymbol().named(name);
    }
}
