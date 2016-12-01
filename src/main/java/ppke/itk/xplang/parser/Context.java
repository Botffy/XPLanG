package ppke.itk.xplang.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parsing context. The state of the parser, encapsulating the symbol table.
 */
public class Context {
    private final static Logger log = LoggerFactory.getLogger("Root.Context");

    private SymbolTable symbolTable = new SymbolTable();

    public Context() {
        log.debug("New context created.");
        openScope();
    }

    public void openScope() {
        symbolTable.openScope();
    }

    public void closeScope() {
        symbolTable.closeScope();
    }

    public void register(Symbol symbol) {
        symbolTable.register(symbol);
    }

    /**
     * Get the Symbols of the language.
     * @return The ordered list of terminal symbols.
     */
    public Iterable<Symbol> getSymbols() {
        return symbolTable.getSymbolList();
    }

    /**
     * Get the Symbol associated with this name (in this scope).
     */
    public Symbol getSymbol(String symbolName) {
        return symbolTable.lookup(symbolName);
    }
}
