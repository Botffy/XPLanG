package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class Output extends Statement {
    public Output(Location location, RValue outputStream, List<RValue> output) {
        super(location);
        this.children.add(0, outputStream);
        this.children.addAll(1, output);
    }

    public RValue getOutputStream() {
        return (RValue) this.children.get(0);
    }

    public List<RValue> getOutputs() {
        return this.children.stream()
            .skip(1)
            .map(x -> (RValue) x)
            .collect(toList());
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
