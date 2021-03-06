package ppke.itk.xplang.parser.operator;

import ppke.itk.xplang.parser.Expression;
import ppke.itk.xplang.parser.ParseError;

public interface Operator {
    int getPrecedence();

    final class Precedence {
        public static final int CONTAINING = Integer.MIN_VALUE;
        public static final int NONE = 0;
        public static final int ASSIGNMENT = 10;
        public static final int CONDITIONAL = 20;
        public static final int LOGIC = 30;
        public static final int RELATIONAL = 40;
        public static final int SUM = 50;
        public static final int PRODUCT = 60;
        public static final int EXPONENT = 70;
        public static final int UNARY_PREFIX = 80;
        public static final int UNARY_POSTFIX = 90;
        public static final int FUNCTION = 100;
        public static final int GROUPING = Integer.MAX_VALUE;

        private Precedence() { /* Private ctor to hide the default one. */ }
    }

    enum Associativity {
        LEFT,
        RIGHT
    }

    interface Prefix extends Operator {
        Expression parsePrefix(ExpressionParser parser) throws ParseError;
    }

    interface Infix extends Operator {
        Expression parseInfix(Expression left, ExpressionParser parser) throws ParseError;
    }
}
