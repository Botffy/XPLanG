package ppke.itk.xplang.parser;

import ppke.itk.xplang.ast.FunctionDeclaration;
import ppke.itk.xplang.type.Signature;
import ppke.itk.xplang.type.Type;

import java.util.*;

public class FunctionSet {
    private final Map<Signature, FunctionDeclaration> set;

    FunctionSet(Map<Signature, FunctionDeclaration> set) {
        this.set = new HashMap<>(set);
    }

    public void limitReturnType(Set<Type> types) {
        Set<Signature> fails = new HashSet<>();
        for(Signature entry : set.keySet()) {
            if(types.stream().noneMatch(t -> t.accepts(entry.getReturnType()))) {
                fails.add(entry);
            }
        }
        fails.forEach(set::remove);
    }

    public void limitArgumentNumberTo(int argNumber) {
        Set<Signature> fails = new HashSet<>();
        for(Signature entry : set.keySet()) {
            if(entry.argumentCount() != argNumber) {
                fails.add(entry);
            }
        }
        fails.forEach(set::remove);
    }

    public void limitArgument(int position, Set<Type> types) {
        Set<Signature> fails = new HashSet<>();
        for(Signature entry : set.keySet()) {
            if(types.stream().noneMatch(t -> entry.argType(position).accepts(t))) {
                fails.add(entry);
            }
        }
        fails.forEach(set::remove);
    }

    public boolean isResolved() {
        return set.size() == 1;
    }

    public boolean isAmbiguous() {
        return set.size() > 1;
    }

    public boolean isEmpty() {
        return set.isEmpty();
    }

    public FunctionDeclaration getOnlyElement() {
        return set.values().iterator().next();
    }
}
