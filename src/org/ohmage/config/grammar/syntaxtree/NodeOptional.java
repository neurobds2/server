//
// Generated by JTB 1.3.2
//

package org.ohmage.config.grammar.syntaxtree;

/**
 * Represents an grammar optional node, e.g. ( A )? or [ A ]
 */
public class NodeOptional implements Node {
	/**
	 * Static-random serialVersionUID.
	 */
	private static final long serialVersionUID = 5462577210633094140L;

	public NodeOptional() {
		node = null;
	}

	public NodeOptional(Node n) {
		addNode(n);
	}

	public void addNode(Node n)  {
		if ( node != null)                // Oh oh!
			throw new Error("Attempt to set optional node twice");

		node = n;
	}
	public void accept(org.ohmage.config.grammar.visitor.Visitor v) {
		v.visit(this);
	}
	public <R,A> R accept(org.ohmage.config.grammar.visitor.GJVisitor<R,A> v, A argu) {
		return v.visit(this,argu);
	}
	public <R> R accept(org.ohmage.config.grammar.visitor.GJNoArguVisitor<R> v) {
		return v.visit(this);
	}
	public <A> void accept(org.ohmage.config.grammar.visitor.GJVoidVisitor<A> v, A argu) {
		v.visit(this,argu);
	}
	public boolean present()   { return node != null; }

	public Node node;
}

