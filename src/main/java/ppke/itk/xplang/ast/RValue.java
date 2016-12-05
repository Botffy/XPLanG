package ppke.itk.xplang.ast;

/**
 * A "right value", named so because it denotes a value that may stand on the right hand side of an assignment. Refers
 * to the value of a memory slot, as opposed to the address of that slot. The latter would be an {@link LValue}.
 */
public abstract class RValue extends Node {}
