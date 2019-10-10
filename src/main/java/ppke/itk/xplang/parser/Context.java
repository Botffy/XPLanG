package ppke.itk.xplang.parser;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.*;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.function.Instruction;
import ppke.itk.xplang.parser.operator.Operator;
import ppke.itk.xplang.type.Archetype;
import ppke.itk.xplang.type.Signature;
import ppke.itk.xplang.type.Type;

import java.util.*;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

/**
 * Parsing context. The state of the parser, encapsulating the symbol table.
 */
public class Context {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Context");

    private final SymbolTable symbolTable = new SymbolTable();
    private final ScopedMap<Name, NameTableEntry> nameTable = new ScopedMap<>();
    private final Map<Symbol, Operator.Prefix> prefixOperators = new HashMap<>();
    private final Map<Symbol, Operator.Infix> infixOperators = new HashMap<>();

    private Type booleanType = Archetype.NONE;
    private Type integerType = Archetype.NONE;

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

     * @throws ParseError if the given name is already taken in the current scope.
     */
    public VariableDeclaration declareVariable(Name name, Token token, Type type) throws ParseError {
        if (nameTable.isFree(name)) {
            VariableDeclaration declaration = new VariableDeclaration(token.location(), name.toString(), type);
            nameTable.add(name, new NameTableEntry(NameTableEntry.EntryType.VARIABLE, declaration));
            log.debug("Declared variable '{}'", name);
            return declaration;
        }

        log.error(
            "Could not register variable by name '{}': name already taken in this scope by {}",
            name,
            nameTable.lookup(name)
        );
        throw new ParseError(token.location(), ErrorCode.NAME_CLASH, name);
    }

    /**
     * Does the given name denote a variable in this scope?
     */
    public boolean isVariable(Name name) {
        NameTableEntry entry = nameTable.lookup(name);
        return entry != null && entry.type == NameTableEntry.EntryType.VARIABLE;
    }

    /**
     * Get a reference to a declared variable.
     * @return The VarRef AST node pointing at the variable.
     * @throws ParseError if the name cannot be found, or does not denote a type.
     */
    public VarRef getVariableReference(Name name, Token token) throws ParseError {
        return new VarRef(token.location(), lookupVariable(name, token));
    }

    /**
     * Get the value of a variable.
     * @return The VarVal AST node pointing at the variable.
     * @throws ParseError if the name cannot be found, or does not denote a type.
     */
    public VarVal getVariableValue(Name name, Token token) throws ParseError {
        return new VarVal(token.location(), lookupVariable(name, token));
    }

    private VariableDeclaration lookupVariable(Name name, Token token) throws ParseError {
        NameTableEntry entry = nameTable.lookup(name);

        if (entry == null) {
            log.info("Lookup of variable '{}' failed: no such declaration.", name);
            throw new ParseError(token.location(), ErrorCode.NO_SUCH_VARIABLE, name);
        }

        if (entry.type != NameTableEntry.EntryType.VARIABLE) {
            log.info("Lookup of variable '{}' failed: found {}", name, entry.type);
            throw new ParseError(token.location(), ErrorCode.NOT_A_VARIABLE, name, entry.type.name());
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
     * @param returnType the return type of the function.
     * @param operands the types of the operands of the function.
     * @throws ParseError when the name is already taken in this scope, or a function by the given signature already exists.
     */
    public void createBuiltin(Name name, Instruction instruction, Type returnType, Type... operands) throws ParseError {
        createBuiltin(name, instruction, returnType, asList(operands));
    }

    /**
     * Create and register a new builtin function under several aliases.
     *
     * @param aliases the names of the functions.
     * @param instruction the instruction to process when the function is called.
     * @param returnType the return type of the function.
     * @param operands the types of the operands of the function.
     * @throws ParseError when the name is already taken in this scope, or a function by the given signature already exists.
     */
    public void createBuiltin(Set<Name> aliases, Instruction instruction, Type returnType, Type... operands) throws ParseError {
        for (Name alias : aliases) {
            createBuiltin(alias, instruction, returnType, asList(operands));
        }
    }

    /**
     * Create and register a new builtin function.
     *
     * @param name the name of the function.
     * @param instruction the instruction to process when the function is called.
     * @param returnType the return type of the function.
     * @param operands the types of the operands of the function.
     * @throws ParseError when the name is already taken in this scope, or a function by the given signature already exists.
     */
    public void createBuiltin(Name name, Instruction instruction, Type returnType, List<Type> operands) throws ParseError {
        Signature signature = new Signature(name, returnType, operands);
        registerFunction(new BuiltinFunction(Location.NONE, signature, instruction));
    }

    /**
     * Register a new function in the current scope.
     *
     * @param function the function to be registered.
     * @throws ParseError when the name is already taken in this scope, or a function by the given signature already exists.
     */
    public void registerFunction(FunctionDeclaration function) throws ParseError {
        Signature signature = function.signature();
        Name name = signature.getName();

        FunctionSet functionSet;
        if (nameTable.isFree(name)) {
            functionSet = new FunctionSet();
            nameTable.add(name, new NameTableEntry(NameTableEntry.EntryType.FUNCTION, functionSet));
        }

        NameTableEntry entry = nameTable.lookup(name);
        if (entry.type != NameTableEntry.EntryType.FUNCTION) {
            log.error(
                "Could not register function '{}': name already taken in this scope by {}",
                signature, nameTable.lookup(name)
            );
            throw new ParseError(Location.NONE, ErrorCode.NAME_CLASH, name);
        }
        functionSet = entry.getValueAsFunctionSet();
        if(functionSet.contains(signature)) {
            log.error("Could not register function '{}': same signature already declared in this scope.", signature);
            throw new ParseError(Location.NONE, ErrorCode.NAME_CLASH, name);
        }

        functionSet.add(function);
        log.debug("Registered function {}", signature);
    }

    /**
     * Does the given name denote a function in this scope?
     */
    public boolean isFunction(Name name) {
        NameTableEntry entry = nameTable.lookup(name);
        return entry != null && entry.type == NameTableEntry.EntryType.FUNCTION;
    }

    /**
     * Get all valid and visible Signatures for a Name.
     */
    public Set<FunctionDeclaration> findFunctionsFor(Name name) {
        return functionSetFor(name).getDeclarations();
    }

    /** Find the FunctionDeclaration node for a signature. */
    public Optional<FunctionDeclaration> lookupFunction(Signature signature) {
        FunctionSet functionSet = functionSetFor(signature.getName());
        return functionSet.find(signature);
    }

    private FunctionSet functionSetFor(Name name) {
        FunctionSet functionSet = new FunctionSet();
        for (NameTableEntry entry : nameTable.allValues(name)) {
            if (entry.type != NameTableEntry.EntryType.FUNCTION) {
                break;
            }

            functionSet.merge(entry.getValueAsFunctionSet());
        }
        return functionSet;
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
     * @throws ParseError if the name is already taken in this scope.
     */
    public void declareType(Name name, Type type) throws ParseError {
        if (nameTable.isFree(name)) {
            nameTable.add(name, new NameTableEntry(NameTableEntry.EntryType.TYPE, type));
            log.debug("Declared type {} as '{}'", type, name);
        } else {
            Object offending = nameTable.lookup(name);
            log.info(
                "Could not register type by name '{}': name already taken in this scope by {}",
                name, offending
            );
            throw new ParseError(Location.NONE, ErrorCode.NAME_CLASH, name);
        }
    }

    public boolean isType(Name name) {
        NameTableEntry entry = nameTable.lookup(name);
        return entry != null && entry.type == NameTableEntry.EntryType.TYPE;
    }

    /**
     * Get a type by its name.
     * @throws ParseError if the name is unknown, or does not denote a type.
     */
    public Type lookupType(Name name, Token token) throws ParseError {
        NameTableEntry entry = nameTable.lookup(name);

        if (entry == null) {
            log.error("Lookup of type '{}' failed.", name);
            throw new ParseError(token.location(), ErrorCode.NO_SUCH_TYPE, name);
        }

        if (entry.type != NameTableEntry.EntryType.TYPE) {
            log.error("Lookup of type '{}' failed: is {}.", name, entry.type);
            throw new ParseError(token.location(), ErrorCode.NOT_A_TYPE, name, entry.type.name());
        }

        Type typ = entry.getValueAsType();
        log.trace("Looked up type for name '{}'", name);
        return typ;
    }

    /**
     * The type acting as the Boolean type in the current context. The boolean type is special, because that's what the
     * conditions expect.
     */
    public Type booleanType() {
        return booleanType;
    }

    public void setBooleanType(Type booleanType) {
        this.booleanType = booleanType;
    }

    /**
     * The type acting as the Integer type in the current context. The integer type is special, because that acts as
     *
     */
    public Type integerType() {
        return integerType;
    }

    public void setIntegerType(Type integerType) {
        this.integerType = integerType;
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
            FUNCTION(FunctionSet.class);

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

        private FunctionSet getValueAsFunctionSet() {
            return (FunctionSet) value;
        }

        @Override
        public String toString() {
            return String.format("%s[%s]", type, value);
        }
    }

}
