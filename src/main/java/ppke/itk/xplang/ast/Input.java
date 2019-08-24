package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class Input extends Statement {
    public Input(Location location, List<Assignment> reading) {
        super(location);
        this.children.addAll(reading);
    }

    public List<Assignment> getAssignments() {
        return this.children.stream()
            .map(x -> (Assignment) x)
            .collect(toList());
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
