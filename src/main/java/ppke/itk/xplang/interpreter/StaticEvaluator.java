package ppke.itk.xplang.interpreter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.ast.Scope;
import ppke.itk.xplang.common.StreamHandler;
import ppke.itk.xplang.parser.Context;

import java.io.FileNotFoundException;
import java.io.Writer;

/**
 * Evaluates a expression ({@link RValue}). Used by the {@link ppke.itk.xplang.parser.Parser}
 * to calculate static expressions at compile time.
 *
 * <p>Uses an {@link Interpreter} to evaluate the given AST fragment over a given {@link Context}.</p>
 */
public class StaticEvaluator {
    private static final Logger log = LoggerFactory.getLogger(StaticEvaluator.class);

    /**
     * Evaluate a static RValue
     * @param rValue the static expression to be evaluated.
     * @param globalConstants the global constants made available during the evaluation.
     * @param <T> the expected type of the returned value. Must be Boolean, Integer, Double, Character or String.
     * @return the calculated value of the rValue
     */
    @SuppressWarnings("unchecked")
    public <T> T eval(RValue rValue, Scope globalConstants, Class<T> expectType) {
        assert rValue.isStatic();

        Interpreter interpreter = new Interpreter(new StaticStreamHandler());

        interpreter.visit(globalConstants);
        rValue.accept(interpreter);

        ScalarValue value = interpreter.getValueStack().pop(ScalarValue.class);
        Object result = value.getValue();
        if (expectType.isInstance(result)) {
            return (T) result;
        }

        log.error("Interpreter produced unexpected value");
        throw new IllegalStateException("Interpreter produced invalid result");
    }

    private static class StaticStreamHandler implements StreamHandler {
        private final String MESSAGE = "Tried I/O during evaluation";

        @Override
        public ProgramInput getStandardInput() {
            return null;
        }

        @Override
        public Writer getStandardOutput() {
            return null;
        }

        @Override
        public ProgramInput getFileInput(String name) throws FileNotFoundException {
            throw new IllegalStateException(MESSAGE);
        }

        @Override
        public Writer getFileOutput(String name) throws FileNotFoundException {
            throw new IllegalStateException(MESSAGE);
        }
    }
}
