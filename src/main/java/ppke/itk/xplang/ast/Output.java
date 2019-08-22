package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class Output extends Statement {
    public Output(Location location, List<RValue> output) {
        super(location);
        this.children.addAll(0, output);
    }

    public List<RValue> getOutputs() {
        return this.children.stream()
            .map(x -> (RValue) x)
            .collect(toList());
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
