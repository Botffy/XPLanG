package ppke.itk.xplang.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.type.Archetype;
import ppke.itk.xplang.type.Signature;
import ppke.itk.xplang.type.Type;

import java.util.*;
import java.util.function.Function;

import static java.util.Collections.nCopies;

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

        if (!expected.accepts(provides.get(root))) {
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
        List<Expression> children = parent.childNodes();

        Map<Signature, List<Boolean>> matches = new HashMap<>();
        for (Signature signature : parent.getCandidates()) {
            List<Boolean> match = new ArrayList<>(nCopies(signature.argumentCount(), false));
            for (int i = 0; i < signature.argumentCount(); ++i) {
                Type expected = signature.getArg(i);
                Type actual = provides.get(children.get(i));
                match.set(i, expected.accepts(actual));
            }
            matches.put(signature, match);
        }

        log.debug("Candidate functions: {}", matches);
        matches.entrySet().stream()
            .filter(x -> x.getValue().contains(false))
            .map(Map.Entry::getKey)
            .forEach(parent::removeFromCandidates);

        Signature found = parent.getResolvedSignature();

        log.debug("{} resolved to {}", parent, found);
        provides.put(parent, found.getReturnType());
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
