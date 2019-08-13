package ppke.itk.xplang.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.FunctionCall;
import ppke.itk.xplang.ast.FunctionDeclaration;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.Archetype;
import ppke.itk.xplang.type.Signature;
import ppke.itk.xplang.type.Type;

import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.Collections.*;
import static java.util.stream.Collectors.toList;

public class TypeChecker {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.TypeChecker");

    private final Context context;
    private final Expression root;
    private final Type expected;
    private final Function<RValue, TypeError> errorMessageProducer;
    private final Map<Expression, Type> provides = new HashMap<>();

    private TypeChecker(Context context, Expression root, Type type, Function<RValue, TypeError> errorMessageProducer) {
        this.context = context;
        this.root = root;
        expected = type;
        this.errorMessageProducer = errorMessageProducer;
    }

    public RValue resolve() throws FunctionResolutionError, TypeError {
        pullingTypes(root);
        log.info("Pulled types: {}", provides);

        Type actual = provides.get(root);
        if (!expected.accepts(actual)) {
            Optional<FunctionDeclaration> conversion = context.lookupFunction(
                new Signature(SpecialName.IMPLICIT_COERCION, expected, provides.get(root))
            );

            if (conversion.isPresent()) {
                RValue Result = root.toASTNode();
                return new FunctionCall(
                    Result.location(),
                    conversion.get(),
                    Result
                );
            }

            throw errorMessageProducer.apply(root.toASTNode());
        }

        return root.toASTNode();
    }

    /**
     * Pulling type information upwards from the leaf nodes.
     */
    private void pullingTypes(Expression node) throws FunctionResolutionError {
        log.debug("Visiting {}", node);

        if (node instanceof FunctionExpression) {
            FunctionExpression parent = (FunctionExpression) node;
            for (Expression child : parent.childNodes()) {
                pullingTypes(child);
            }

            resolveFunction(parent);
        } else if (node instanceof ValueExpression) {
            ValueExpression leaf = (ValueExpression) node;
            Type type = leaf.getType();
            provides.put(node, type);
        }
    }

    private void resolveFunction(FunctionExpression parent) throws FunctionResolutionError {
        log.debug("Resolving function {}", parent.getName());

        parent.removeFromCandidatesIf(x -> x.argumentCount() != parent.childNodes().size());

        Map<Signature, List<MatchType>> matches = new HashMap<>();
        for (Signature signature : parent.getCandidates()) {
            List<MatchType> match = calculateMatch(signature, parent.childNodes());
            matches.put(signature, match);
        }

        Set<Signature> originalCandidates = new HashSet<>(parent.getCandidates());

        log.debug("Candidate functions: {}", matches);
        matches.entrySet().stream()
            .filter(x -> x.getValue().contains(MatchType.NONE))
            .map(Map.Entry::getKey)
            .forEach(parent::removeFromCandidates);

        if (parent.hasNoCandidates()) {
            throw new NoViableFunctionException(
                parent.getName(),
                originalCandidates,
                parent.childNodes().stream().map(provides::get).collect(toList()),
                parent.getLocation());
        }

        if (parent.isNotResolved()) {
            disambiguateCoercedCandidates(parent, matches);

            if (parent.isNotResolved()) {
                throw new FunctionAmbiguousException(
                    parent.getName(),
                    parent.getCandidates(),
                    parent.childNodes().stream().map(provides::get).collect(toList()),
                    parent.getLocation());
            }
        }

        Signature found = parent.getOnlyCandidate();
        coerceArguments(parent, found, matches.get(found));

        log.debug("{} resolved to {}", parent, found);
        provides.put(parent, found.getReturnType());
    }

    private List<MatchType> calculateMatch(Signature signature, List<Expression> children) {
        List<MatchType> match = new ArrayList<>(nCopies(signature.argumentCount(), MatchType.NONE));
        for (int i = 0; i < signature.argumentCount(); ++i) {
            Type expected = signature.getArg(i);
            Type actual = provides.get(children.get(i));

            if (expected.accepts(actual)) {
                match.set(i, MatchType.MATCHED);
            } else {
                match.set(i, context.lookupFunction(new Signature(SpecialName.IMPLICIT_COERCION, expected, actual))
                    .map(x -> MatchType.COERCED)
                    .orElse(MatchType.NONE)
                );
            }
        }
        return match;
    }

    private void disambiguateCoercedCandidates(FunctionExpression node, Map<Signature, List<MatchType>> matches) {
        for (Map.Entry<Signature, List<MatchType>> f1 : matches.entrySet()) {
            for (Map.Entry<Signature, List<MatchType>> f2 : matches.entrySet()) {
                if (f1 == f2) {
                    continue;
                }

                List<MatchType> f1Arg = f1.getValue();
                List<MatchType> f2Arg = f2.getValue();
                IntSummaryStatistics summary = IntStream
                    .range(0, f1Arg.size())
                    .map(i -> f1Arg.get(i).compareTo(f2Arg.get(i)))
                    .filter(x -> x != 0)
                    .summaryStatistics();

                int max = summary.getMax();
                int min = summary.getMin();

                // f1 is in some cases worse, in some cases better than f2, so it's not unambiguous
                if (max != min) {
                    continue;
                }

                // f1 is always better, and never worse than f2
                if (max == -1) {
                    log.debug("Removing {}: {}", f2.getKey(), f2.getValue());
                    node.removeFromCandidates(f2.getKey());
                }
            }
        }
    }

    private void coerceArguments(FunctionExpression parent, Signature signature, List<MatchType> match) {
        for (int i = 0; i < match.size(); ++i) {
            if (match.get(i) == MatchType.COERCED) {
                Expression original = parent.childNodes().get(i);

                Signature coercionSignature = new Signature(SpecialName.IMPLICIT_COERCION, signature.getArg(i), provides.get(original));
                FunctionDeclaration coercionFunction = context.lookupFunction(coercionSignature).orElseThrow(IllegalStateException::new);

                Expression coercion = new FunctionExpression(
                    SpecialName.IMPLICIT_COERCION,
                    Location.NONE,
                    singleton(coercionFunction),
                    singletonList(original)
                );
                parent.childNodes().set(i, coercion);
            }
        }
    }

    private enum MatchType {
        MATCHED,
        COERCED,
        NONE
    }


    public static Builder in(Context context) {
        return new Builder(context);
    }

    public static class Builder {
        private final Context context;
        private Expression expression;
        private Type expectedType;
        private Function<RValue, TypeError> errorMessageProducer;

        Builder(Context context) {
            this.context = context;
        }

        public Builder checking(Expression expression) {
            this.expression = expression;
            return this;
        }

        public Builder expecting(Type type) {
            expectedType = type;
            return this;
        }

        public Builder withCustomErrorMessage(Function<RValue, TypeError> errorMessageProducer) {
            this.errorMessageProducer = errorMessageProducer;
            return this;
        }

        public TypeChecker build() {
            Objects.requireNonNull(expression, "Must supply an expression to typecheck!");

            if (expectedType == null) {
                expectedType = Archetype.ANY;
            }

            if (errorMessageProducer == null) {
                errorMessageProducer = (rValue) -> new TypeError(expectedType, rValue.getType(), rValue.location());
            }

            return new TypeChecker(context, expression, expectedType, errorMessageProducer);
        }
    }
}
