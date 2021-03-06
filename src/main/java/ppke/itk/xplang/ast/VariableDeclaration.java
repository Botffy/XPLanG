package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.Type;

import java.util.Optional;

/**
 * A variable declaration.
 *
 * In the interpreter, this will trigger reserving space for a value, and will also serve as a (conceptual) memory
 * address for that value.
 */
public final class VariableDeclaration extends Node {
    private final String variableName;
    private final Type type;

    public VariableDeclaration(Location location, String variableName, Type type) {
        this(location, variableName, type, null);
    }

    public VariableDeclaration(Location location, String variableName, Type type, RValue initialValue) {
        super(location);
        this.variableName = variableName;
        this.type = type;
        this.children.add(0, initialValue);
    }

    public Optional<RValue> getInitialValue() {
        return Optional.ofNullable((RValue) this.children.get(0));
    }

    /**
     * The name by which the variable was declared. It has zero effect on execution (the variable should be referenced
     * by referencing this object), but it's useful for debugging and pretty-printing and such.
     * @return The name of the variable as it appeared in the source code.
     */
    public String getName() {
        return variableName;
    }

    /**
     * The type of the variable.
     */
    public Type getType() {
        return type;
    }

    @Override public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override public String toString() {
        return String.format("VARIABLEDECLARATION[%s]", getName());
    }
}
