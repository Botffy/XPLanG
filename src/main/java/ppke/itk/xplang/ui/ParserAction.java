package ppke.itk.xplang.ui;

import ppke.itk.xplang.ast.Root;
import ppke.itk.xplang.lang.Grammar;
import ppke.itk.xplang.lang.PlangGrammar;
import ppke.itk.xplang.parser.Context;
import ppke.itk.xplang.parser.Parser;
import ppke.itk.xplang.parser.Symbol;

import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;

class ParserAction implements Action {
    private final Grammar grammar = new PlangGrammar();

    @Override
    public List<Action> execute() {
        Context context = new Context();
        grammar.setup(context);
        Reader source = new StringReader("PROGRAM testing <_< >_> program_vége");

        try {
            Parser parser = new Parser(source, context);
            grammar.S(parser);
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }

        return Collections.emptyList();
    }
}
