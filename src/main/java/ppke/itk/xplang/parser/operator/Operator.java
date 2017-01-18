package ppke.itk.xplang.parser.operator;

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

        private Precedence() { /* Private ctor to hide the default one. */ }
    }

    enum Associativity {
        LEFT,
        RIGHT
    }

    interface Prefix extends Operator {
        void parsePrefix(ExpressionParser parser);
    }

    interface Infix extends Operator {
        void parseInfix(ExpressionParser parser) throws ParseError;
    }
}
