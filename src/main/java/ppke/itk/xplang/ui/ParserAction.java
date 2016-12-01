package ppke.itk.xplang.ui;

import ppke.itk.xplang.lang.Grammar;
import ppke.itk.xplang.parser.Context;
import ppke.itk.xplang.parser.Parser;
import ppke.itk.xplang.parser.Symbol;

import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

class ParserAction implements Action {
    private final Grammar grammar = new Grammar() {
        @Override
        public void setup(Context context) {
            context.register(new Symbol("FUNNY_STARE_RIGHT", Pattern.compile(">_>")));
            context.register(new Symbol("FUNNY_STARE_LEFT",  Pattern.compile("<_<")));
            context.register(new Symbol("SHRUGGIE",  Pattern.compile("¯\\\\_\\(ツ\\)_\\/¯", Pattern.UNICODE_CHARACTER_CLASS)));
            context.register(new Symbol("DISAPPROVAL_LOOK",  Pattern.compile("ಠ_ಠ", Pattern.UNICODE_CHARACTER_CLASS)));
            context.register(new Symbol("WS",  Pattern.compile("\\s+"), Symbol.Precedence.DEFAULT, false));
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
