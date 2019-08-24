package ppke.itk.xplang.parser;

import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.Type;

import java.util.Collections;
import java.util.List;

/**
 * A terminal node of the expression tree.
 */
public class ValueExpression extends Expression {
    private final RValue value;

    public ValueExpression(RValue value) {
        this.value = value;
    }

    public Type getType() {
        return value.getType();
    }

    @Override
    public List<Expression> childNodes() {
        return Collections.emptyList();
    }

    public RValue getRValue() {
        return value;
    }

    @Override
    RValue toASTNode() {
        return value;
    }

    @Override
    public Location getLocation() {
        return value.location();
    }

    @Override
    public String toString() {
        return String.format("ValueExpression[%s]", value.toString());
    }
}
