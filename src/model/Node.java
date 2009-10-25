package model;

import java.util.ArrayList;

public class Node {
	
	private long id;
	
	private String name;
	
	private boolean state;
	
	private String sa;
	
	/**
	 * Custo do caminho até este nó (g)
	 */
	private long cost;
	
	/**
	 * Heurística dada (h)
	 */
	private long heuristic;
	
	/**
	 * Vértice pai (usado para encontrar o caminho percorrido)
	 */
	private Node father;
	
	/**
	 * Filhos deste vértice
	 */
	private ArrayList<Node> child;
	
	/**
	 * Tabela de Roteamento
	 * @return
	 */
	private ArrayList<Table> tabela;
	
	private ArrayList<Packet> pacotes;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public String getSa() {
		return sa;
	}

	public void setSa(String sa) {
		this.sa = sa;
	}

	public long getCost() {
		return cost;
	}

	public void setCost(long cost) {
		this.cost = cost;
	}

	public long getHeuristic() {
		return heuristic;
	}

	public void setHeuristic(long heuristic) {
		this.heuristic = heuristic;
	}

	public Node getFather() {
		return father;
	}

	public void setFather(Node father) {
		this.father = father;
	}

	public ArrayList<Node> getChild() {
		return child;
	}

	public void setChild(ArrayList<Node> child) {
		this.child = child;
	}

	public ArrayList<Table> getTabela() {
		return tabela;
	}

	public void setTabela(ArrayList<Table> tabela) {
		this.tabela = tabela;
	}

	public ArrayList<Packet> getPacotes() {
		return pacotes;
	}

	public void setPacotes(ArrayList<Packet> pacotes) {
		this.pacotes = pacotes;
	}
	
	public ArrayList<Edge> getVizinhos(ArrayList<Edge> edges) {
		ArrayList<Edge> tmp = new ArrayList<Edge>();
		for (Edge edge : edges) {
			if (this == edge.getSrc() || this == edge.getDst())
				tmp.add(edge);
		}
		return tmp;
	}
	
}
