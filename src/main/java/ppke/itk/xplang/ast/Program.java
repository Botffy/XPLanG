package ppke.itk.xplang.ast;

public class Program extends Node {
    @Override public void accept(ASTVisitor visitor) { visitor.visit(this); }

    private final String name;

    public Program(String name) {
        this.name = name;
    }

    @Override public String toString() {
        return String.format("Program %s", name == null? "main" : name);
    }
}
