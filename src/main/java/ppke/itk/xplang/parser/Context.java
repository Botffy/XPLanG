package ppke.itk.xplang.parser;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.Scope;
import ppke.itk.xplang.ast.VarRef;
import ppke.itk.xplang.ast.VarVal;
import ppke.itk.xplang.ast.VariableDeclaration;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.common.Translator;
import ppke.itk.xplang.type.Type;

import static java.util.stream.Collectors.toList;

/**
 * Parsing context. The state of the parser, encapsulating the symbol table.
 */
public class Context {
    private final static Translator translator = Translator.getInstance("Parser");
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Context");

    private SymbolTable symbolTable = new SymbolTable();
    private ScopedMap<String, Object> nameTable = new ScopedMap<>();

    public Context() {
        log.debug("New context created.");
        openScope();
    }

    public void openScope() {
        symbolTable.openScope();
        nameTable.openScope();
    }

    public Scope closeScope() {
        // variables declared in the current scope:
        Scope scope = new Scope(
            nameTable.entriesInCurrentScope().stream()
                .map(Pair::getValue)
                .filter(x -> x instanceof VariableDeclaration)
                .map(x -> (VariableDeclaration) x)
                .collect(toList()
            )
        );

        symbolTable.closeScope();
        nameTable.closeScope();
        return scope;
    }

    public void declareVariable(Token token) throws NameClashError {
        String name = token.lexeme();
        if(nameTable.isFree(name)) {
            VariableDeclaration declaration = new VariableDeclaration(name);
            nameTable.add(name, declaration);
            log.debug("Declared variable '{}'", name);
        } else {
            log.error(
                "Could not register variable by name '{}': name already taken in this scope by {}",
                name, nameTable.lookup(name)
            );
            throw new NameClashError(token);
        }
    }

    public VarRef getVariableReference(Token variableName) throws NameError {
        return new VarRef(lookupVariable(variableName));
    }

    public VarVal getVariableValue(Token variableName) throws NameError {
        return new VarVal(lookupVariable(variableName));
    }

    private VariableDeclaration lookupVariable(Token token) throws NameError {
        String variableName = token.lexeme();
        Object obj = nameTable.lookup(variableName);
        if(obj instanceof VariableDeclaration) {
            VariableDeclaration var = (VariableDeclaration) obj;
            log.trace("Looked up variable for name '{}'", variableName);
            return var;
        }
        // TODO mention it if it exist but it's not a variable
        log.error("Lookup of variable '{}' failed.", variableName);
        throw new NameError("No variable named %s", token);
    }

    public void declareType(String name, Type type) throws NameClashError {
        if(nameTable.isFree(name)) {
            nameTable.add(name, type);
            log.debug("Declared type {] as '{}'", type, name);
        } else {
            Object offending = nameTable.lookup(name);
            log.error(
                "Could not register type by name '{}': name already taken in this scope by {}",
                name, offending
            );
            throw new NameClashError(
                translator.translate("parser.TypeNameAlreadyTaken.message", name, offending),
                new Location(-1, -1) // FIXME, massive fixme
            );
        }
    }

    public Type lookupType(Token token) throws NameError {
        String typeName = token.lexeme();
        Object obj = nameTable.lookup(typeName);
        if(obj instanceof Type) {
            Type typ = (Type) obj;
            log.trace("Looked up type for name '{}'", typeName);
            return typ;
        }
        log.error("Lookup of type '{}' failed.", typeName);
        throw new NameError("No type named %s", token);
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
