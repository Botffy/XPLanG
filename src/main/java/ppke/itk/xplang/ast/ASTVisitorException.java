package ppke.itk.xplang.ast;

public abstract class ASTVisitorException extends Exception {
    public ASTVisitorException(String message) {
        super(message);
    }

    public ASTVisitorException(String message, Throwable cause) {
        super(message, cause);
    }
}
