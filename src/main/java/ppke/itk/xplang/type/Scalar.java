package ppke.itk.xplang.type;

public class Scalar extends Type {
    public static final Scalar INTEGER = new Scalar("IntegerType");
    public static final Scalar BOOLEAN = new Scalar("BooleanType");

    public Scalar(String label) {
        super(label);
    }
}
