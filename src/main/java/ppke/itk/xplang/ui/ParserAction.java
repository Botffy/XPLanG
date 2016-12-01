package ppke.itk.xplang.ui;

import ppke.itk.xplang.lang.Grammar;
import ppke.itk.xplang.parser.Context;
import ppke.itk.xplang.parser.Parser;
import ppke.itk.xplang.parser.Symbol;

import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;

class ParserAction implements Action {
    private final Grammar grammar = new Grammar() {
        @Override
        public void setup(Context context) {
            createSymbol().named("FUNNY_STARE_RIGHT").matchingLiteral(">_>").register(context);
            createSymbol().named("FUNNY_STARE_LEFT") .matchingLiteral("<_<").register(context);
            createSymbol().named("SHRUGGIE")         .matchingLiteral("¯\\_(ツ)_/¯").register(context);
            createSymbol().named("DISAPPROVAL_LOOK") .matchingLiteral("ಠ_ಠ").register(context);
            createSymbol().named("WHITESPACE").matching("\\s+").notSignificant().register(context);
        }

        @Override
        public void S(Parser parser) {}
    };

    @Override
    public List<Action> execute() {
        Context context = new Context();
        grammar.setup(context);
        Reader source = new StringReader("<_< >_>\nಠ_ಠ ¯\\_(ツ)_/¯ ಠ_ಠ\nಠ_ಠ\nಠ_ಠ\n¯\\_(ツ)_/¯ öö¯\\_(ツ)_/¯ ¯\\_(ツ)_/¯");

        try {
            Parser parser = new Parser(source, context);
            while(!parser.actual().getSymbol().equals(Symbol.EOF)) {
                System.out.println(parser.actual());
                parser.advance();
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }

        return Collections.emptyList();
    }
}
