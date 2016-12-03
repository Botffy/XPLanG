package ppke.itk.xplang.ui;

import ppke.itk.xplang.ast.Root;
import ppke.itk.xplang.lang.Grammar;
import ppke.itk.xplang.lang.PlangGrammar;
import ppke.itk.xplang.parser.Context;
import ppke.itk.xplang.parser.ParseError;
import ppke.itk.xplang.parser.Parser;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class ParserAction implements Action {
    private final Grammar grammar = new PlangGrammar();

    @Override
    public List<Action> execute() {
        Context context = new Context();
        grammar.setup(context);
        Reader source = new StringReader("PROGRAM testing <_< >_> >_> \n >_> >_> program_vége");

        try {
            Parser parser = new Parser(source, context);
            Root root = grammar.S(parser);
            return Arrays.asList(
                new ASTPrinting(root),
                new Interpreting(root)
            );
        } catch(ParseError e) {
            return Collections.singletonList(new MessagePrinter(e.getMessage()));
        }
    }
}
