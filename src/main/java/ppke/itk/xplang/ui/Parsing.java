package ppke.itk.xplang.ui;

import ppke.itk.xplang.ast.Root;
import ppke.itk.xplang.lang.PlangGrammar;
import ppke.itk.xplang.parser.Grammar;
import ppke.itk.xplang.parser.ParseError;
import ppke.itk.xplang.parser.Parser;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class Parsing implements Action {
    private final Grammar grammar = new PlangGrammar();

    @Override
    public List<Action> execute() {
        Reader source = new StringReader("PROGRAM testing <_< >_> >_> \n >_> >_> program_vége");
        Parser parser = new Parser();
        try {
            Root root = parser.parse(source, grammar);
            return Arrays.asList(
                new ASTPrinting(root),
                new Interpreting(root)
            );
        } catch(ParseError e) {
            return Collections.singletonList(new MessagePrinting(e.getMessage()));
        }
    }
}
