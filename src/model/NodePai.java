package model;

public class NodePai {
	
	public NodePai(Node node, Node pai) {
		this.setNode(node);
		this.setPai(pai);
	}
	
	private Node node;
	private Node pai;
	/**
	 * @return the node
	 */
	public Node getNode() {
		return node;
	}
	/**
	 * @param node the node to set
	 */
	public void setNode(Node node) {
		this.node = node;
	}
	/**
	 * @return the pai
	 */
	public Node getPai() {
		return pai;
	}
	/**
	 * @param pai the pai to set
	 */
	public void setPai(Node pai) {
		this.pai = pai;
	}
}
