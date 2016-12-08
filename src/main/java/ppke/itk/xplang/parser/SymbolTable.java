package ppke.itk.xplang.parser;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.util.Counter;

import java.util.*;

/**
 * A data structure to store the terminal symbols of the language.
 */
class SymbolTable {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Context.SymbolTable");

    private static final class Entry {
        final Symbol symbol;
        final int ordinal;

        private Entry(Symbol symbol, int ordinal) {
            this.symbol = symbol;
            this.ordinal = ordinal;
        }

        @Override public String toString() {
            return symbol.toString();
        }
    }

    private static Comparator<Entry> declarationOrder = Comparator.comparingInt(e -> e.ordinal);

    private final Counter symbolCounter = new Counter();
    private final ScopedMap<String, Entry> table = new ScopedMap<>();

    private final List<Symbol> symbolList = new ArrayList<>();
    private final Map<String, Symbol> symbolMap = new HashMap<>();

    /**
     * Opens a new lexical scope.
     */
    void openScope() {
        table.openScope();
    }

    /**
     * Closes the current lexical scope. Symbols registered in this scope are invalidated.
     */
    void closeScope() throws ScopedMap.NoScopeError {
        table.closeScope();
        rebuildCollections();
    }

    /**
     *  Registers a new symbol.
     *  @param symbol the symbol to register.
     */
    void register(Symbol symbol) {
        log.debug("Registering symbol for name '{}'", symbol.getName());
        Entry entry = new Entry(symbol, symbolCounter.advance());
        table.add(symbol.getName(), entry);
        rebuildCollections();
    }

    /**
     *  Look up a symbol by name.
     */
    Symbol lookup(String name) {
        if(!symbolMap.containsKey(name)) {
            log.error("Failed to access {}, no such symbol", name);
            throw new RuntimeException(String.format("No symbol named %s", name));
        }
        log.trace("Looked up symbol for name {}", name);
        return symbolMap.get(name);
    }

    /**
     *  Get the list of all currently visible symbols.
     *
     *  @return the list currently visible symbols in the order they were registered. The returned object is guaranteed
     *  to be the same over executions, and never becomes invalid as long as the SymbolList is in a valid state. The
     *  returned list is not modifiable.
     */
    List<Symbol> getSymbolList() {
        return Collections.unmodifiableList(symbolList);
    }

    /**
     *  Get a Name-&gt;Symbol mapping of all currently visible symbols.
     *
     *  @return the unmodifiable mapping of currently visible symbols. The returned object is guaranteed to be the same
     *  over executions, and never becomes invalid as long as the SymbolList is in a valid state. The returned map is
     *  not modifiable.
     */
    Map<String, Symbol> getSymbolMap() {
        return Collections.unmodifiableMap(symbolMap);
    }

    private void rebuildCollections() {
        log.debug("Rebuilding collections");
        symbolList.clear();
        symbolMap.clear();
        Set<Pair<String, Entry>> set = table.entries();
        set.stream().map(Pair::getValue).sorted(declarationOrder).forEach(x -> {
            symbolList.add(x.symbol);
            symbolMap.put(x.symbol.getName(), x.symbol);
        });
    }
}
