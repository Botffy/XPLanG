package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Stream.concat;

/**
 * A conditional statement.
 */
public final class Conditional extends Statement {
    private final int numberOfBranches;

    public Conditional(Location location, Branch branch, List<Branch> branches, Sequence elseSequence) {
        super(location);
        numberOfBranches = 1 + branches.size();
        concat(Stream.of(branch), branches.stream()).forEach(this::addBranch);

        if (elseSequence != null) {
            this.children.add(elseSequence);
        }
    }

    public List<Branch> getBranches() {
        List<Branch> result = new ArrayList<>();
        for (int i = 0; i < numberOfBranches; ++i) {
            result.add(new Branch(
                (RValue) this.children.get(2 * i),
                (Sequence) this.children.get(2 * i + 1)
            ));
        }
        return result;
    }

    public Sequence getElseSequence() {
        return (Sequence) children.get(numberOfBranches * 2);
    }

    @Override public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    private void addBranch(Branch branch) {
        this.children.add(branch.getCondition());
        this.children.add(branch.getSequence());
    }

    /**
     * A condition / sequence pair.
     */
    public static class Branch {
        private final RValue condition;
        private final Sequence sequence;

        public Branch(RValue condition, Sequence sequence) {
            this.condition = condition;
            this.sequence = sequence;
        }

        public RValue getCondition() {
            return condition;
        }

        public Sequence getSequence() {
            return sequence;
        }

        @Override
        public String toString() {
            return "";
        }
    }
}
