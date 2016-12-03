package ppke.itk.xplang.ast;

import org.apache.commons.lang3.StringUtils;

/**
 * Very simple AST printer.
 *
 * We make use of the normalised nature of the AST: instead of laboriously implementing {@link ASTVisitor}, we just
 * iterate over the set of children.
 */
public final class ASTPrinter {
    private int indentation;

    private void indent() {
        indentation += 2;
    }
    private void dedent() {
        indentation -= 2;
        if(indentation < 0) throw new RuntimeException("Tried to dedent when there was no indent!");
    }

    private void print(Node node) {
        System.out.println(String.format(
            "%s%s",
            StringUtils.repeat(' ', indentation),
            node.getClass().getSimpleName().toUpperCase()
        ));
    }

    public void visit(Node node) {
        print(node);
        indent();
        for(Node child : node.getChildren()) {
            visit(child);
        }
        dedent();
    }
}
