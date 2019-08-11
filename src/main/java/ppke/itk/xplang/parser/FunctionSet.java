package ppke.itk.xplang.parser;

import ppke.itk.xplang.ast.FunctionDeclaration;
import ppke.itk.xplang.type.Signature;

import java.util.*;

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

    Set<FunctionDeclaration> getDeclarations() {
        return new HashSet<>(set.values());
    }

    Set<Signature> getSignatures() {
        return unmodifiableSet(set.keySet());
    }

    Optional<FunctionDeclaration> find(Signature signature) {
        return Optional.ofNullable(set.get(signature));
    }

    void limitArgumentNumberTo(int argNumber) {
        set.entrySet().removeIf(x -> x.getKey().argumentCount() != argNumber);
    }
}
