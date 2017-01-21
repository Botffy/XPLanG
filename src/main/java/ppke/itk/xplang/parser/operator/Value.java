package ppke.itk.xplang.parser.operator;

import ppke.itk.xplang.ast.RValue;

public class Value extends Expression {
    private final RValue value;

    protected Value(RValue value) {
        super(value.location());
        this.value = value;
    }

    @Override protected RValue toASTNode() {
        return value;
    }
}
