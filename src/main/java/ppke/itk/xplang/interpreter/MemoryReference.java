package ppke.itk.xplang.interpreter;

import ppke.itk.xplang.ast.VariableDeclaration;

class MemoryReference implements ReferenceValue {
    private final Memory memory;
    private final VariableDeclaration variable;

    MemoryReference(Memory memory, VariableDeclaration variable) {
        this.memory = memory;
        this.variable = variable;
    }

     VariableDeclaration getVariable() {
        return variable;
    }

    @Override public void assign(Value value) {
        memory.setValue(this.variable, value);
    }

    @Override public Value copy() {
        return null;
    }

    @Override public String toString() {
        return String.format("[Memory address %s]", variable.getName());
    }

    @Override public int hashCode() {
        return variable.hashCode();
    }

    @Override public boolean equals(Object object) {
        return object instanceof MemoryReference && ((MemoryReference) object).variable == this.variable;
    }
}
