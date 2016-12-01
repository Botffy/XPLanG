package ppke.itk.xplang.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.Scope;

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

    public Scope closeScope() {
        symbolTable.closeScope();
        return new Scope();
    }

    public void register(Symbol symbol) {
        symbolTable.register(symbol);
        log.debug("Registered symbol {}", symbol);
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
    public Symbol lookup(String symbolName) {
        return symbolTable.lookup(symbolName);
    }
}
