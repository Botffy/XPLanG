package ppke.itk.xplang.parser;


import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.util.Counter;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * A key-value map structure to keep track of lexical scoping. Based on the
 * <a href="http://dl.acm.org/citation.cfm?id=1314012">LeBlanc-Cook symbol table</a>.
 *
 * Implemented as a generic data structure because we're going to store symbols in them too! XPLanG is going to be
 * extensible at runtime.
 */
class ScopedMap<Key, Value> {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Context.ScopedMap");

    static class NoScopeError extends RuntimeException {
        NoScopeError() {
            super("Tried to close nonexistent scope.");
        }
    }
    static class NameNotFreeError extends RuntimeException {
        NameNotFreeError(Object obj) {
            super(String.format("The key '%s' already exists in this scope", obj));
        }
    }

    private static final class Scope {
        final int identifier;

        private Scope(int identifier) {
            this.identifier = identifier;
        }
    }

    private static final class Entry<T> {
        final int scopeId;
        final T data;

        private Entry(int scopeId, T data) {
            this.scopeId = scopeId;
            this.data = data;
        }
    }

    private final Counter scopeIdCounter = new Counter();
    private final List<Scope> scopeStack = new ArrayList<>();
    private final Map<Key, List<Entry<Value>>> symtab = new HashMap<>();

    /**
     * Open a new lexical scope.
     */
    void openScope() {
        scopeStack.add(new Scope(scopeIdCounter.advance()));
        log.debug("Opened scope");
    }

    /**
     * Closes the current lexical scope.
     * @throws NoScopeError if there is no open scope.
     */
    void closeScope() throws NoScopeError {
        if(scopeStack.isEmpty()) throw new NoScopeError();
        scopeStack.remove(scopeStack.size() - 1);
        log.debug("Closed scope");
    }

    /**
     * Registers a new object in the current scope.
     * @param name the name the data will be associated with.
     * @param value the object to be registered.
     * @throws NameNotFreeError if the name is already taken in the current scope.
     */
    void add(Key name, Value value) throws NameNotFreeError {
        Entry<Value> entry = new Entry<>(currentScope().identifier, value);

        List<Entry<Value>> chain;
        if(symtab.containsKey(name)) {
            chain = symtab.get(name);

            if(chain.isEmpty() || top(chain).scopeId != currentScope().identifier) {
                // either the name was never used, or it's used in an earlier scope so we can shadow
                chain.add(entry);
                log.debug("Added '{}' for name '{}'", value, name);
            } else {
                log.debug("Failed to add '{}' for name '{}': name already taken in this scope", value, name);
                throw new NameNotFreeError(name);
            }
        } else {
            chain = new ArrayList<>();
            chain.add(entry);
            symtab.put(name, chain);
            log.debug("Added '{}' for name '{}' (started new chain)", value, name);
        }
    }

    /**
     * Searches the symbol table for given name, and returns the matching entry.
     * @param name the name to be looked up.
     * @return the closest value associated with the name, or null if the key is not in the table.
     */
    Value lookup(Key name) {
        if(!symtab.containsKey(name)) return null;

        Entry<Value> match = null;
        List<Entry<Value>> chain = symtab.get(name);
        for(Entry<Value> entry : chain) {
            for(int i = scopeStack.size(); i-- > 0;) {
                if(entry.scopeId == scopeStack.get(i).identifier) {
                    match = entry;
                    break;
                }
            }
        }

        return match == null? null : match.data;
    }

    /**
     * Is the given name free in the current scope, or has there been something already registered by that name?
     * @param name The name to be queried.
     * @return True if the name hasn't been registered in the current scope, false otherwise.
     */
    boolean isFree(Key name) {
        if(!symtab.containsKey(name)) return true;
        List<Entry<Value>> chain = symtab.get(name);
        return !(!chain.isEmpty() && (top(chain).scopeId == currentScope().identifier));
    }

    /**
     * Returns all entries currently visible from the current scope.
     */
    Set<Pair<Key, Value>> entries() {
        Set<Pair<Key, Value>> Result = new HashSet<>();
        for(Map.Entry<Key, List<Entry<Value>>> mapEntry : symtab.entrySet()) {
            List<Entry<Value>> chain = mapEntry.getValue();
            Entry<Value> match = null;
            for(Entry<Value> entry : chain) {
                for(int i = scopeStack.size(); i-- > 0;) {
                    if(entry.scopeId == scopeStack.get(i).identifier) {
                        match = entry;
                        break;
                    }
                }
            }

            if(match != null) {
                Result.add(Pair.of(mapEntry.getKey(), match.data));
            }
        }
        return Result;
    }

    /**
     * Returns all valid values for a specified key, including hidden ones.
     */
    List<Value> allValues(Key name) {
        if(!symtab.containsKey(name)) return Collections.emptyList();

        List<Value> Result = new ArrayList<>();
        List<Entry<Value>> chain = symtab.get(name);
        for(Entry<Value> entry : chain) {
            for(int i = scopeStack.size(); i --> 0;) {
                if(entry.scopeId == scopeStack.get(i).identifier) {
                    Result.add(entry.data);
                }
            }
        }

        return Result;
    };

    /**
     * Returns all entries declared in the current scope.
     */
    Set<Pair<Key, Value>> entriesInCurrentScope() {
        Set<Pair<Key, Value>> Result = new HashSet<>();
        for(Map.Entry<Key, List<Entry<Value>>> mapEntry : symtab.entrySet()) {
            List<Entry<Value>> chain = mapEntry.getValue();
            Entry<Value> match = null;
            for(Entry<Value> entry : chain) {
                if(entry.scopeId == currentScope().identifier) {
                    match = entry;
                    break;
                }
            }

            if(match != null) {
                Result.add(Pair.of(mapEntry.getKey(), match.data));
            }
        }
        return Result;
    }

    private Scope currentScope() {
        if(scopeStack.isEmpty()) throw new NoScopeError();
        return scopeStack.get(scopeStack.size() - 1);
    }

    private static <T> T top(List<T> stack) {
        if(stack.isEmpty()) return null;
        return stack.get(stack.size() - 1);
    }
}
