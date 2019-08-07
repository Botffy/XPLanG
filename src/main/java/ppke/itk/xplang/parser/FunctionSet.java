package ppke.itk.xplang.parser;

import ppke.itk.xplang.ast.FunctionDeclaration;
import ppke.itk.xplang.type.Signature;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;

final class FunctionSet {
    private final Map<Signature, FunctionDeclaration> set = new HashMap<>();

    void add(FunctionDeclaration function) {
        set.putIfAbsent(function.signature(), function);
    }

    boolean contains(Signature signature) {
        return set.containsKey(signature);
    }

    void merge(FunctionSet that) {
        for(FunctionDeclaration function : that.set.values()) {
            this.add(function);
        }
    }

    Map<Signature, FunctionDeclaration> getDeclarations() {
        return unmodifiableMap(set);
    }

    Set<Signature> getSignatures() {
        return unmodifiableSet(set.keySet());
    }

    void limitArgumentNumberTo(int argNumber) {
        set.entrySet().removeIf(x -> x.getKey().argumentCount() != argNumber);
    }
}
