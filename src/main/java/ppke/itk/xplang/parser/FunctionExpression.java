package ppke.itk.xplang.parser;

import ppke.itk.xplang.ast.FunctionCall;
import ppke.itk.xplang.ast.FunctionDeclaration;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.Signature;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class FunctionExpression extends Expression {
    private final Location location;
    private final Name name;
    private final Set<FunctionDeclaration> candidates;
    private List<Expression> childNodes;

    public FunctionExpression(Name name, Location location, Set<FunctionDeclaration> candidates, List<Expression> childNodes) {
        this.name = name;
        this.location = location;
        this.candidates = new HashSet<>(candidates);
        this.childNodes = new ArrayList<>(childNodes);
    }

    @Override
    public Location getLocation() {
        return location;
    }

    public Name getName() {
        return name;
    }

    Set<Signature> getCandidates() {
        return candidates.stream()
            .map(FunctionDeclaration::signature)
            .collect(Collectors.toSet());
    }

    /**
     * Remove one from the signature functions.
     */
    void removeFromCandidates(Signature toRemove) {
        candidates.removeIf(x -> x.signature().equals(toRemove));
    }

    void removeFromCandidatesIf(Predicate<Signature> predicate) {
        candidates.removeIf(x -> predicate.test(x.signature()));
    }

    boolean hasNoCandidates() {
        return candidates.isEmpty();
    }

    boolean isNotResolved() {
        return candidates.size() != 1;
    }

    Signature getOnlyCandidate() {
        if (candidates.size() != 1) {
            throw new IllegalStateException("Function is not resolved");
        }

        return candidates.iterator().next().signature();
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
        FunctionDeclaration declaration = candidates.iterator().next();
        return new FunctionCall(location, declaration, childNodes.stream().map(Expression::toASTNode).collect(toList()));
    }

    @Override
    public String toString() {
        return String.format("FunctionExpression[%s]", name);
    }
}
