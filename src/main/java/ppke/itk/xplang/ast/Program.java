package ppke.itk.xplang.ast;

public class Program extends Node {
    @Override public void accept(ASTVisitor visitor) { visitor.visit(this); }

    private final String name;

    public Program(String name, Block block) {
        this.name = name;
        children.add(0, block);
    }

    public Block block() {
        return (Block) children.get(0);
    }

    @Override public String toString() {
        return String.format("Program %s", name == null? "main" : name);
    }
}
