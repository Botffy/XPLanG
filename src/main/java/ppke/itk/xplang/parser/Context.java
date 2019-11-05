package ppke.itk.xplang.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.*;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.function.Instruction;
import ppke.itk.xplang.parser.operator.Operator;
import ppke.itk.xplang.type.Archetype;
import ppke.itk.xplang.type.Signature;
import ppke.itk.xplang.type.Type;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Parsing context, the current state of the parser.
 *
 * <p>The parser context keeps track of declared functions, types, variables, and operators. Functions, types and
 * operators are always global. Variables are always local, and can only be declared if there is a local scope
 * open.</p>
 */
public class Context {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Context");

    private final Map<Name, Type> types = new HashMap<>();
    private final Map<Name, FunctionSet> functions = new HashMap<>();
    private Map<Name, VariableDeclaration> globalConstants = new HashMap<>();
    private Map<Name, VariableDeclaration> localVariables = null;

    private final Map<Symbol, Operator.Prefix> prefixOperators = new HashMap<>();
    private final Map<Symbol, Operator.Infix> infixOperators = new HashMap<>();

    private Type booleanType = Archetype.NONE;
    private Type integerType = Archetype.NONE;

    public Context() {
        log.debug("Global scope opened.");
    }

    /**
     * Open a local scope.
     */
    public void openScope() {
        if (localVariables != null) {
            throw new IllegalStateException("A local scope is already open");
        }

        localVariables = new HashMap<>();
    }

    /**
     * Close the current local scope.
     *
     * @return The AST representation of the variable declarations (a {@link Scope} node).
     */
    public Scope closeScope() {
        Scope scope = new Scope(localVariables.values());
        localVariables = null;
        return scope;
    }

    private boolean isFree(Name name) {
        return !types.containsKey(name)
            && !functions.containsKey(name)
            && !globalConstants.containsKey(name)
            && (localVariables == null || !localVariables.containsKey(name));
    }

    public void declareConstant(Name name, VariableDeclaration declaration) throws ParseError {
        log.debug("Declaring constant '{}'", name);
        if (!isFree(name)) {
            log.info("Could not register constant by name '{}'", name);
            throw new ParseError(declaration.location(), ErrorCode.NAME_CLASH, name);
        }

        globalConstants.put(name, declaration);
        log.debug("Declared constant '{}'", name);
    }

    public Scope getGlobalConstants() {
        return new Scope(globalConstants.values());
    }

    public boolean isConstant(Name name) {
        return globalConstants.containsKey(name);
    }

    /**
     * Declare a new variable.
     *
     * <p>A variable can only be declared if there is a local scope open (there are no global
     * variables in PLanG), and the name is free in both the global and the local scope (shadowing is not allowed in
     * PLanG).</p>
     *
     * @throws ParseError with ErrorCode NAME_CLASH if the given name is already taken.
     */
    public void declareVariable(Name name, VariableDeclaration declaration) throws ParseError {
        log.debug("Declaring variable '{}'", name);
        if (localVariables == null) {
            throw new IllegalStateException("Tried to declare variable with no local scope open.");
        }

        if (!isFree(name)) {
            log.info("Could not register variable by name '{}': name already taken", name);
            throw new ParseError(declaration.location(), ErrorCode.NAME_CLASH, name);
        }

        localVariables.put(name, declaration);
        log.debug("Declared variable '{}'", name);
    }

    /**
     * Does the given name denote a variable in this scope?
     */
    public boolean isVariable(Name name) {
        return (localVariables != null && localVariables.containsKey(name)) || isConstant(name);
    }

    /**
     * Get a reference to a declared variable.
     * @return The VarRef AST node pointing at the variable.
     * @throws ParseError if the name cannot be found, or if it denotes a constant.
     */
    public VarRef getVariableReference(Name name, Token token) throws ParseError {
        if (isConstant(name)) {
            throw new ParseError(token.location(), ErrorCode.CONSTANT_CANNOT_BE_LVALUE, name);
        }

        return new VarRef(token.location(), lookupVariable(name, token));
    }

    /**
     * Get the value of a variable.
     * @return The VarVal AST node pointing at the variable.
     * @throws ParseError if the name cannot be found, or does not denote a type.
     */
    public VarVal getVariableValue(Name name, Token token) throws ParseError {
        return new VarVal(token.location(), lookupVariable(name, token), isConstant(name));
    }

    public VariableDeclaration lookupVariable(Name name, Token token) throws ParseError {
        Optional<VariableDeclaration> var = Optional.empty();
        var = Optional.ofNullable(localVariables == null ? null : localVariables.get(name));
        if (!var.isPresent()) {
            var = Optional.ofNullable(globalConstants.get(name));
        }

        if (!var.isPresent()) {
            log.info("Lookup of variable '{}' failed.", name);
            throw new ParseError(token.location(), ErrorCode.NO_SUCH_VARIABLE, name);
        }

        log.trace("Looked up variable for name '{}'", name);
        return var.get();
    }

    /**
     * Create and register a new builtin function.
     *
     * @param name the name of the function.
     * @param instruction the instruction to process when the function is called.
     * @param returnType the return type of the function.
     * @param parameters the types of the operands of the function.
     * @throws ParseError when the name is already taken in this scope, or a function by the given signature already exists.
     */
    public void createBuiltin(Name name, Instruction instruction, Type returnType, Type... parameters) throws ParseError {
        Signature signature = new Signature(name, returnType, parameters);
        registerFunction(new BuiltinFunction(Location.NONE, signature, instruction));
    }

    /**
     * Create and register a new builtin function under several aliases.
     *
     * @param aliases the names of the functions.
     * @param instruction the instruction to process when the function is called.
     * @param returnType the return type of the function.
     * @param parameters the types of the operands of the function.
     * @throws ParseError when the name is already taken in this scope, or a function by the given signature already exists.
     */
    public void createBuiltin(Set<Name> aliases, Instruction instruction, Type returnType, Type... parameters) throws ParseError {
        for (Name alias : aliases) {
            createBuiltin(alias, instruction, returnType, parameters);
        }
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
        if (!functions.containsKey(name) && !isFree(name)) {
            log.info("Could not register function '{}': name already taken", signature);
            throw new ParseError(function.location(), ErrorCode.NAME_CLASH, name);
        }

        FunctionSet functionSet = functions.computeIfAbsent(name, x -> new FunctionSet());
        if(functionSet.contains(signature)) {
            log.info("Could not register function '{}': same signature already declared.", signature);
            throw new ParseError(function.location(), ErrorCode.NAME_CLASH, name);
        }

        functionSet.add(function);
        log.debug("Registered function {}", signature);
    }

    /**
     * Does the given name denote a function?
     */
    public boolean isFunction(Name name) {
        return functions.containsKey(name);
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
        if (!functions.containsKey(name)) {
            return new FunctionSet();
        }
        return functions.get(name);
    }

    /**
     * Register a new prefix operator.
     * @param symbol the lexical symbol denoting the operator.
     * @param operator the operator object to be registered.
     */
    public void prefix(Symbol symbol, Operator.Prefix operator) {
        prefixOperators.put(symbol, operator);
    }

    public void removePrefix(Symbol symbol) {
        prefixOperators.remove(symbol);
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
        if (!isFree(name)) {
            log.info("Could not register type by name '{}': name already taken", name);
            throw new ParseError(Location.NONE, ErrorCode.NAME_CLASH, name);
        }

        types.put(name, type);
        log.debug("Declared type {} as '{}'", type, name);
    }

    /**
     * Does the given name denote a type?
     */
    public boolean isType(Name name) {
        return types.containsKey(name);
    }

    /**
     * Get a type by its name.
     * @throws ParseError if the name is unknown, or does not denote a type.
     */
    public Type lookupType(Name name, Token token) throws ParseError {
        Type type = types.get(name);

        if (type == null) {
            log.info("Lookup of type '{}' failed", name);
            throw new ParseError(token.location(), ErrorCode.NO_SUCH_TYPE, name);
        }

        log.trace("Looked up type for name '{}'", name);
        return type;
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
}
