package ppke.itk.xplang.ast;

/**
 * An algorithm applied to the {@link ppke.itk.xplang.ast AST}.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Visitor_pattern">Visitor pattern</a>
 */
public interface ASTVisitor {
    void visit(Scope scope);
    void visit(Root root);
    void visit(Program program);
    void visit(Sequence sequence);
    void visit(Block block);

    void visit(Incrementation incrementation);
    void visit(Decrementation decrementation);
}
