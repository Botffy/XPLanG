package ppke.itk.xplang.type;

import ppke.itk.xplang.parser.Name;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

/**
 * The signature of a function: its name, and the inputs and outputs of the function.
 *
 * <p>If the returnType is {@link Archetype#NONE}, then the function does not return a value, it is a procedure.</p>
 */
public class Signature {
    private final Name name;
    private final Type returnType;
    private final List<Type> parameters;

    public Signature(Name name, Type returnType, List<Type> parameters) {
        this.name = name;
        this.returnType = returnType;
        this.parameters = unmodifiableList(new ArrayList<>(parameters));
    }

    public Signature(Name name, Type returnType, Type... parameters) {
        this(name, returnType, asList(parameters));
    }

    public Name getName() {
        return name;
    }

    public Type getReturnType() {
        return returnType;
    }

    public List<Type> getParameters() {
        return parameters;
    }

    public Type getParameter(int i) {
        return parameters.get(i);
    }

    public Type parameterType(int position) {
        return parameters.get(position);
    }

    public int parameterCount() {
        return parameters.size();
    }

    @Override public boolean equals(Object object) {
        if(!(object instanceof Signature)) return false;
        Signature that = (Signature) object;
        return this.name.equals(that.name)
            && this.returnType.equals(that.returnType)
            && this.parameters.equals(that.parameters);
    }

    @Override public int hashCode() {
        return Objects.hash(name, returnType, parameters);
    }

    @Override public String toString() {
        return String.format("%s: %s -> %s", name, parameters, returnType);
    }
}
