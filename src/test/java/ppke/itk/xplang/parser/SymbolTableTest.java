package ppke.itk.xplang.parser;

import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;
import static org.junit.Assert.*;

public class SymbolTableTest {
    @Test
    public void initialieEmpty() {
        SymbolTable sl = new SymbolTable();
        sl.openScope();

        assertTrue("The symbol list should be empty", sl.getSymbolList().isEmpty());
        assertTrue("The symbol map should be empty", sl.getSymbolMap().isEmpty());
    }

    @Test
    public void symbolsCanBeRegistered() {
        SymbolTable sl = new SymbolTable();
        Symbol s = new Symbol("IDENTIFIER", Pattern.compile("a+"));
        sl.openScope();
        sl.register(s);

        assertTrue("The symbol lists should contain my freshly registered symbol", sl.getSymbolList().contains(s));
        assertEquals(
            "The symbol mapping should contain my freshly registered symbol",
            s, sl.getSymbolMap().get(s.getName())
        );
    }

    @Test
    public void exposedCollectionsAreChanging() {
        SymbolTable sl = new SymbolTable();
        sl.openScope();

        List<Symbol> symbolList = sl.getSymbolList();
        Map<String, Symbol> symbolMap = sl.getSymbolMap();

        Symbol s = new Symbol("IDENTIFIER", Pattern.compile("a+"));
        sl.register(s);

        assertEquals(
            "The symbol map acquired earlier should now contain my freshly registered symbol",
            s, symbolMap.get(s.getName())
        );
        assertTrue(
            "The symbol list acquired earlier should now contain my freshly registered symbol",
            symbolList.contains(s)
        );
    }

    @Test
    public void exposedCollectionsAreUnmodifiable() {
        SymbolTable sl = new SymbolTable();
        sl.openScope();

        List<Symbol> symbolList = sl.getSymbolList();
        Map<String, Symbol> symbolMap = sl.getSymbolMap();

        Throwable t1 = exceptionThrownBy(() -> symbolList.add(new Symbol("IDENTIFIER", Pattern.compile("a+"))));
        Throwable t2 = exceptionThrownBy(() -> symbolMap.put("IDENTIFIER", new Symbol("IDENTIFIER", Pattern.compile("a+"))));
    }

    @Test
    public void symbolsAreScoped() {
        SymbolTable sl = new SymbolTable();
        sl.openScope();

        Symbol id1 = new Symbol("IDENTIFIER", Pattern.compile("a+"));
        Symbol id2 = new Symbol("IDENTIFIER", Pattern.compile("a[a-z]+"));

        sl.register(id1);
        sl.openScope();
        sl.register(id2);

        assertEquals(
            "There should be one symbol visible",
            1, sl.getSymbolList().size()
        );

        assertEquals(
            "The visible symbol should be the one declared later",
            id2, sl.getSymbolList().get(0)
        );
    }
}
