package ppke.itk.xplang.type;

import ppke.itk.xplang.parser.Name;

import java.util.List;
import java.util.Optional;

public class RecordType extends Type {
    private final List<Field> fields;

    public RecordType(String label, List<Field> fields) {
        super(label);
        this.fields = fields;
    }

    public List<Field> getFields() {
        return fields;
    }

    public Optional<Field> getField(Name name) {
        return fields.stream()
            .filter(x -> x.getName().equals(name))
            .findFirst();
    }

    /**
     * Records have strict nominal equivalence.
     */
    @Override
    public boolean accepts(Type that) {
        return this == that;
    }

    @Override
    public Initialization getInitialization() {
        return Initialization.RECORD;
    }

    public static class Field {
        private final Name name;
        private final Type type;

        public Field(Name name, Type type) {
            this.name = name;
            this.type = type;
        }

        public Name getName() {
            return name;
        }

        public Type getType() {
            return type;
        }
    }
}
