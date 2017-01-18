package ppke.itk.xplang.parser;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.*;
import ppke.itk.xplang.common.CursorPosition;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.common.Translator;
import ppke.itk.xplang.function.Instruction;
import ppke.itk.xplang.parser.operator.Operator;
import ppke.itk.xplang.type.Signature;
import ppke.itk.xplang.type.Type;

import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * Parsing context. The state of the parser, encapsulating the symbol table.
 */
public class Context {
    private final static Translator translator = Translator.getInstance("Parser");
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Context");

    private final SymbolTable symbolTable = new SymbolTable();
    private final ScopedMap<Name, Object> nameTable = new ScopedMap<>();
    private final Map<Symbol, Operator.Prefix> prefixOperators = new HashMap<>();
    private final Map<Symbol, Operator.Infix> infixOperators = new HashMap<>();

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

    public void declareVariable(Name name, Token token, Type type) throws NameClashError {
        if(nameTable.isFree(name)) {
            VariableDeclaration declaration = new VariableDeclaration(token.location(), name.toString(), type);
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

    public VarRef getVariableReference(Name name, Token token) throws NameError {
        return new VarRef(token.location(), lookupVariable(name, token));
    }

    public VarVal getVariableValue(Name name, Token token) throws NameError {
        return new VarVal(token.location(), lookupVariable(name, token));
    }

    private VariableDeclaration lookupVariable(Name name, Token token) throws NameError {
        Object obj = nameTable.lookup(name);
        if(obj instanceof VariableDeclaration) {
            VariableDeclaration var = (VariableDeclaration) obj;
            log.trace("Looked up variable for name '{}'", name);
            return var;
        }
        // TODO mention it if it exist but it's not a variable
        log.error("Lookup of variable '{}' failed.", name);
        throw new NameError("No variable named %s", token);
    }

    private static final class FuncSet {
        private final Map<Signature, FunctionDeclaration> set = new HashMap<>();

        void add(FunctionDeclaration function) {
            if(!set.containsKey(function.signature())) {
                set.put(function.signature(), function);
            }
        }

        boolean contains(Signature signature) {
            return set.containsKey(signature);
        }

        void merge(FuncSet that) {
            for(FunctionDeclaration function : that.set.values()) {
                this.add(function);
            }
        }

        FunctionSet toFunctionSet() {
            return new FunctionSet(set);
        }
    }

    public void createBuiltin(Name name, Instruction instruction, Type returns, Type... args) throws NameClashError {
        Signature sig = new Signature(name.toString(), returns, args);

        FuncSet funcSet;
        if(nameTable.isFree(name)) {
            funcSet = new FuncSet();
            nameTable.add(name, funcSet);
        }

        Object obj = nameTable.lookup(name);
        if(!(obj instanceof FuncSet)) {
            log.error(
                "Could not register builtin by name '{}': name already taken in this scope by {}",
                name, nameTable.lookup(name)
            );
            throw new NameClashError("Could not register builtin function", Location.NONE);
        }
        funcSet = (FuncSet) obj;
        if(funcSet.contains(sig)) {
            log.error("Could not register builtin by name '{}': same signature already declared in this scope.");
            throw new NameClashError("Could not register builtin function", Location.NONE);
        }

        funcSet.add(new BuiltinFunction(Location.NONE, sig, instruction));
        log.debug("Registered builtin function {} with signature {}", name, sig);
    }

    /**
     * Get all valid and visible Signatures for a Name.
     */
    public FunctionSet lookupFunction(Name name) {
        FuncSet funcSet = new FuncSet();
        for(Object object : nameTable.allValues(name)) {
            if(!(object instanceof FuncSet)) break;

            funcSet.merge((FuncSet) object);
        }
        return funcSet.toFunctionSet();
    }

    public void prefix(Symbol symbol, Operator.Prefix operator) {
        prefixOperators.put(symbol, operator);
    }

    public Operator.Prefix prefixOf(Symbol symbol) {
        return prefixOperators.get(symbol);
    }

    public void infix(Symbol symbol, Operator.Infix operator) {
        infixOperators.put(symbol, operator);
    }

    public Operator.Infix infixOf(Symbol symbol) {
        return infixOperators.get(symbol);
    }

    public void declareType(Name name, Type type) throws NameClashError {
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
                new CursorPosition(-1,-1).toUnaryLocation() // FIXME, massive fixme
            );
        }
    }

    public Type lookupType(Name name, Token token) throws NameError {
        Object obj = nameTable.lookup(name);
        if(obj instanceof Type) {
            Type typ = (Type) obj;
            log.trace("Looked up type for name '{}'", name);
            return typ;
        }
        log.error("Lookup of type '{}' failed.", name);
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
