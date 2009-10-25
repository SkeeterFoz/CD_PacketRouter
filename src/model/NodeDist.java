package model;

public class NodeDist implements Comparable<NodeDist> {
	
	public NodeDist(Node node, int dist) {
		this.setNode(node);
		this.setDistancia(dist);
	}
	
	private Node node;
	private int distancia;
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
	 * @return the distancia
	 */
	public int getDistancia() {
		return distancia;
	}
	/**
	 * @param distancia the distancia to set
	 */
	public void setDistancia(int distancia) {
		this.distancia = distancia;
	}
	
	@Override
	public int compareTo(NodeDist o) {
		return this.getDistancia() < o.getDistancia() ? 0 : 1;
	}
}
