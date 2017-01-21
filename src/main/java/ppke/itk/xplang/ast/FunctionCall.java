package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.Type;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class FunctionCall extends RValue {
    private final FunctionDeclaration function;

    public FunctionCall(Location location, FunctionDeclaration function, RValue... args) {
        super(location);
        this.function = function;
        this.children.addAll(0, Arrays.asList(args));
    }

    public FunctionDeclaration getDeclaration() {
        return function;
    }

    public List<RValue> arguments() {
        return children.subList(0, function.signature().getArgs().size()).stream()
            .map(x -> (RValue) x)
            .collect(toList());
    }

    @Override public Type getType() {
        return function.signature().getReturnType();
    }

    @Override public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override public String toString() {
        return String.format("%s[%s]", super.toString(), function.signature());
    }
}
