package ppke.itk.xplang.ast;

import ppke.itk.xplang.common.Location;

public class Program extends Node {
    private final String name;

    public Program(Location location, String name, Block block) {
        super(location);
        this.name = name;
        children.add(0, block);
    }

    public Block block() {
        return (Block) children.get(0);
    }

    @Override public String toString() {
        return String.format("%s[%s]", super.toString(), name == null? "main" : name);
    }

    @Override public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
