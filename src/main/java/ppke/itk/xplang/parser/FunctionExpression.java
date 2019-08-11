package ppke.itk.xplang.parser;

import ppke.itk.xplang.ast.FunctionCall;
import ppke.itk.xplang.ast.FunctionDeclaration;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.Signature;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toList;

public class FunctionExpression extends Expression {
    private final Location location;
    private final Name name;
    private final Map<Signature, FunctionDeclaration> candidates;
    private List<Expression> childNodes;

    public FunctionExpression(Location location, FunctionDeclaration declaration, List<Expression> childNodes) {
        this.location = location;
        this.name = declaration.signature().getName();
        this.candidates = new HashMap<>();
        candidates.put(declaration.signature(), declaration);
        this.childNodes = childNodes;
    }

    public FunctionExpression(Name name, Location location, Map<Signature, FunctionDeclaration> candidates, List<Expression> childNodes) {
        this.name = name;
        this.location = location;
        this.candidates = new HashMap<>(candidates);
        this.childNodes = childNodes;
    }

    public Location getLocation() {
        return location;
    }

    public Name getName() {
        return name;
    }

    public Set<Signature> getCandidates() {
        return unmodifiableSet(candidates.keySet());
    }

    /**
     * Remove one from the signature functions.
     */
    void removeFromCandidates(Signature toRemove) {
        candidates.remove(toRemove);
    }

    boolean hasNoCandidates() {
        return candidates.isEmpty();
    }

    boolean isNotResolved() {
        return candidates.size() != 1;
    }

    Signature getOnlyCandidate() throws FunctionResolutionError {
        if (candidates.size() > 1) {
            throw new FunctionAmbiguousException(name, candidates.keySet(), location);
        }

        return candidates.entrySet().stream()
            .findAny()
            .map(Map.Entry::getKey)
            .orElseThrow(() -> new NoViableFunctionException(name, location));
    }


    @Override
    public List<Expression> childNodes() {
        return childNodes;
    }

    public void setChildNodes(List<Expression> childNodes) {
        this.childNodes = childNodes;
    }

    @Override
    public RValue toASTNode() {
        FunctionDeclaration declaration = candidates.entrySet().stream()
            .findAny()
            .map(Map.Entry::getValue)
            .get();
        return new FunctionCall(location, declaration, childNodes.stream().map(Expression::toASTNode).collect(toList()));
    }

    @Override
    public String toString() {
        return String.format("FunctionExpression[%s]", name);
    }

}
