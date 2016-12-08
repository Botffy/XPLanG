/**
 * Abstract syntax tree.
 *
 * This is a normalized, heterogeneous AST: 'heterogeneous' because the nodes are of different types (all specific nodes
 * are implemented as different descendant classes), and 'normalized' because the nodes can be handled via a
 * homogeneous interface.
 *
 * We don't keep track of a node's parent because it would only complicate maintaining the coherence of the hierarchy.
 * If an {@link ppke.itk.xplang.ast.ASTVisitor} would need parent information, that visitor should build an auxiliary
 * structure for that.
 */
package ppke.itk.xplang.ast;
