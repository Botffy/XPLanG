package ppke.itk.xplang.parser.operator;

import ppke.itk.xplang.ast.RValue;

import java.util.Collections;
import java.util.List;

public class Value extends Expression {
    private final RValue value;

    public Value(RValue value) {
        this.value = value;
    }

    @Override protected List<Expression> childNodes() {
        return Collections.emptyList();
    }

    @Override public RValue toASTNode() {
        return value;
    }
}
