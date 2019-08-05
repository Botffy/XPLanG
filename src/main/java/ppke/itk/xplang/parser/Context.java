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

import javax.swing.text.html.Option;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * Parsing context. The state of the parser, encapsulating the symbol table.
 */
public class Context {
    private final static Translator translator = Translator.getInstance("Parser");
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Context");

    private final SymbolTable symbolTable = new SymbolTable();
    private final ScopedMap<Name, NameTableEntry> nameTable = new ScopedMap<>();
    private final Map<Symbol, Operator.Prefix> prefixOperators = new HashMap<>();
    private final Map<Symbol, Operator.Infix> infixOperators = new HashMap<>();

    public Context() {
        log.debug("New context created.");
        openScope();
    }

    /**
     * Open a new lexical scope.
     */
    public void openScope() {
        symbolTable.openScope();
        nameTable.openScope();
    }

    /**
     * Close the current scope.
     *
     * @return The AST representation of the variable declarations (a {@link Scope} node).
     */
    public Scope closeScope() {
        Scope scope = new Scope(
            nameTable.entriesInCurrentScope().stream()
                .map(Pair::getValue)
                .filter(x -> x.type == NameTableEntry.EntryType.VARIABLE)
                .map(NameTableEntry::getValueAsVariable)
                .collect(toList()
            )
        );

        symbolTable.closeScope();
        nameTable.closeScope();
        return scope;
    }

    /**
     * Declare a new variable.

     * @throws NameClashError if the given name is already taken in the current scope.
     */
    public void declareVariable(Name name, Token token, Type type) throws NameClashError {
        if (nameTable.isFree(name)) {
            VariableDeclaration declaration = new VariableDeclaration(token.location(), name.toString(), type);
            nameTable.add(name, new NameTableEntry(NameTableEntry.EntryType.VARIABLE, declaration));
            log.debug("Declared variable '{}'", name);
        } else {
            log.error(
                "Could not register variable by name '{}': name already taken in this scope by {}",
                name,
                nameTable.lookup(name)
            );
            throw new NameClashError(token);
        }
    }

    /**
     * Get a reference to a declared variable.
     * @return The VarRef AST node pointing at the variable.
     * @throws NameError if the name cannot be found, or does not denote a type.
     */
    public VarRef getVariableReference(Name name, Token token) throws NameError {
        return new VarRef(token.location(), lookupVariable(name, token));
    }

    /**
     * Get the value of a variable.
     * @return The VarVal AST node pointing at the variable.
     * @throws NameError if the name cannot be found, or does not denote a type.
     */
    public VarVal getVariableValue(Name name, Token token) throws NameError {
        return new VarVal(token.location(), lookupVariable(name, token));
    }

    private VariableDeclaration lookupVariable(Name name, Token token) throws NameError {
        NameTableEntry entry = nameTable.lookup(name);

        if (entry == null) {
            log.info("Lookup of variable '{}' failed: no such declaration.", name);
            throw new NoSuchVariableException(name, token);
        }

        if (entry.type != NameTableEntry.EntryType.VARIABLE) {
            log.info("Lookup of variable '{}' failed: found {}", name, entry.type);
            throw new NotVariableException(name, entry.type.name(), token);
        }

        VariableDeclaration var = entry.getValueAsVariable();
        log.trace("Looked up variable for name '{}'", name);
        return var;
    }

    /**
     * Create and register a new builtin function.
     *
     * @param name the name of the function.
     * @param instruction the instruction to process when the function is called.
     * @param returns the return type of the function.
     * @param args the arguments of the function.
     * @throws NameClashError when the name is already taken in this scope, or a function by the given signature already exists.
     */
    public void createBuiltin(Name name, Instruction instruction, Type returns, Type... args) throws NameClashError {
        Signature sig = new Signature(name.toString(), returns, args);

        FuncSet funcSet;
        if (nameTable.isFree(name)) {
            funcSet = new FuncSet();
            nameTable.add(name, new NameTableEntry(NameTableEntry.EntryType.FUNCTION, funcSet));
        }

        NameTableEntry entry = nameTable.lookup(name);
        if (entry.type != NameTableEntry.EntryType.FUNCTION) {
            log.error(
                "Could not register builtin by name '{}': name already taken in this scope by {}",
                name, nameTable.lookup(name)
            );
            throw new NameClashError("Could not register builtin function", Location.NONE);
        }
        funcSet = entry.getValueAsFuncSet();
        if(funcSet.contains(sig)) {
            log.error("Could not register builtin by name '{}': same signature already declared in this scope.", name);
            throw new NameClashError("Could not register builtin function", Location.NONE);
        }

        funcSet.add(new BuiltinFunction(Location.NONE, sig, instruction));
        log.debug("Registered builtin function {} with signature {}", name, sig);
    }

    /**
     * Get all valid and visible Signatures for a Name.
     */
    @Deprecated
    public FunctionSet lookupFunction(Name name) {
        FuncSet funcSet = new FuncSet();
        for (NameTableEntry entry : nameTable.allValues(name)) {
            if (entry.type != NameTableEntry.EntryType.FUNCTION) {
                break;
            }

            funcSet.merge(entry.getValueAsFuncSet());
        }
        return funcSet.toFunctionSet();
    }

    /**
     * Register a new prefix operator.
     * @param symbol the lexical symbol denoting the operator.
     * @param operator the operator object to be registered.
     */
    public void prefix(Symbol symbol, Operator.Prefix operator) {
        prefixOperators.put(symbol, operator);
    }

    /**
     * Get the prefix operator registered for a symbol.
     */
    public Optional<Operator.Prefix> prefixOf(Symbol symbol) {
        return Optional.ofNullable(prefixOperators.get(symbol));
    }

    /**
     * Register a new infix operator.
     * @param symbol the lexical symbol denoting the operator.
     * @param operator the operator object to be registered.
     */
    public void infix(Symbol symbol, Operator.Infix operator) {
        infixOperators.put(symbol, operator);
    }

    /**
     * Get the infix operator registered for a symbol.
     */
    public Optional<Operator.Infix> infixOf(Symbol symbol) {
        return Optional.ofNullable(infixOperators.get(symbol));
    }

    /**
     * Declare a new type by the given name.
     * @throws NameClashError if the name is already taken in this scope.
     */
    public void declareType(Name name, Type type) throws NameClashError {
        if (nameTable.isFree(name)) {
            nameTable.add(name, new NameTableEntry(NameTableEntry.EntryType.TYPE, type));
            log.debug("Declared type {} as '{}'", type, name);
        } else {
            Object offending = nameTable.lookup(name);
            log.info(
                "Could not register type by name '{}': name already taken in this scope by {}",
                name, offending
            );
            throw new NameClashError(
                translator.translate("parser.TypeNameAlreadyTaken.message", name, offending),
                Location.NONE
            );
        }
    }

    /**
     * Get a type by its name.
     * @throws NameError if the name is unknown, or does not denote a type.
     */
    public Type lookupType(Name name, Token token) throws NameError {
        NameTableEntry entry = nameTable.lookup(name);

        if (entry == null) {
            log.error("Lookup of type '{}' failed.", name);
            throw new NameError("No type named %s", token);
        }

        if (entry.type != NameTableEntry.EntryType.TYPE) {
        }

        Type typ = entry.getValueAsType();
        log.trace("Looked up type for name '{}'", name);
        return typ;
    }

    /**
     * Register a new terminal symbol in the language.
     */
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

    private static final class NameTableEntry {
        enum EntryType {
            VARIABLE(VariableDeclaration.class),
            TYPE(Type.class),
            FUNCTION(FuncSet.class);

            private final Class<?> clazz;
            EntryType(Class<?> clazz) {
                this.clazz = clazz;
            }
        }

        private final EntryType type;
        private final Object value;

        NameTableEntry(EntryType type, Object value) {
            if (!type.clazz.isInstance(value)) {
                throw new IllegalStateException(String.format(
                    "Tried to store %s as a %s", value.getClass().getSimpleName(), type
                ));
            }

            this.type = type;
            this.value = value;
        }

        private VariableDeclaration getValueAsVariable() {
            return (VariableDeclaration) value;
        }

        private Type getValueAsType() {
            return (Type) value;
        }

        private FuncSet getValueAsFuncSet() {
            return (FuncSet) value;
        }
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
}
