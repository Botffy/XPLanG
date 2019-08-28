package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.Type;

public class ConditionalConnective extends RValue {
    private final Op operator;

    public ConditionalConnective(Location location, Op operator, RValue left, RValue right) {
        super(location);
        this.operator = operator;
        this.children.add(0, left);
        this.children.add(1, right);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    public RValue getLeft() {
        return (RValue) this.children.get(0);
    }

    public RValue getRight() {
        return (RValue) this.children.get(1);
    }

    public Op operator() {
        return operator;
    }

    @Override
    public Type getType() {
        return ((RValue) this.children.get(0)).getType();
    }

    public static enum Op {
        AND(false),
        OR(true);

        public final boolean stopValue;

        Op(boolean stopValue) {
            this.stopValue = stopValue;
        }
    }
}
