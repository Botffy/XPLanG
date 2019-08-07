package ppke.itk.xplang.type;

import ppke.itk.xplang.parser.Name;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

/**
 * The signature of a function: its name, and the inputs and outputs of the function.
 */
public class Signature {
    private final Name name;
    private final Type returnType;
    private final List<Type> args;

    public Signature(Name name, Type returnType, List<Type> args) {
        this.name = name;
        this.returnType = returnType;
        this.args = unmodifiableList(new ArrayList<>(args));
    }

    public Signature(Name name, Type returnType, Type... args) {
        this(name, returnType, asList(args));
    }

    public Name getName() {
        return name;
    }

    public Type getReturnType() {
        return returnType;
    }

    public List<Type> getArgs() {
        return args;
    }

    public Type getArg(int i) {
        return args.get(i);
    }

    public Type argType(int position) {
        return args.get(position);
    }

    public int argumentCount() {
        return args.size();
    }

    @Override public boolean equals(Object object) {
        if(!(object instanceof Signature)) return false;
        Signature that = (Signature) object;
        return this.name.equals(that.name)
            && this.returnType.equals(that.returnType)
            && this.args.equals(that.args);
    }

    @Override public int hashCode() {
        return Objects.hash(name, returnType, args);
    }

    @Override public String toString() {
        return String.format("%s: %s -> %s", name, args, returnType);
    }
}
