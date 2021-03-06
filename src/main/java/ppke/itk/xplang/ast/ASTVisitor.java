package ppke.itk.xplang.ast;

/**
 * An algorithm applied to the {@link ppke.itk.xplang.ast AST}.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Visitor_pattern">Visitor pattern</a>
 */
public interface ASTVisitor {
    void visit(Root root);
    void visit(Program program);
    void visit(Scope scope);
    void visit(VariableDeclaration variableDeclaration);
    void visit(BuiltinFunction function);
    void visit(Function function);
    void visit(Sequence sequence);
    void visit(Block block);
    void visit(Assignment assignment);
    void visit(Conditional conditional);
    void visit(Loop loop);
    void visit(Input input);
    void visit(Output output);
    void visit(Assertion assertion);
    void visit(ExpressionStatement expressionStatement);
    void visit(FunctionCall functionCall);
    void visit(ConditionalConnective conditionalConnective);
    void visit(VarRef varRef);
    void visit(ElementRef elementRef);
    void visit(VarVal varVal);
    void visit(ElementVal elementVal);
    void visit(Slice slice);
    void visit(IntegerLiteral integerLiteral);
    void visit(RealLiteral realLiteral);
    void visit(BooleanLiteral booleanLiteral);
    void visit(CharacterLiteral characterLiteral);
    void visit(StringLiteral stringLiteral);
    void visit(StandardInput standardInput);
    void visit(StandardOutput standardOutput);
    void visit(OldVariableValue oldVariableValue);
}
