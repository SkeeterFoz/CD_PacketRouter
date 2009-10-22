package controller;

import java.util.ArrayList;

import model.Edge;
import model.Heuristic;
import model.Node;

public class Graph {
	
	private ArrayList<Node> nodelist;
	
	private ArrayList<Edge> edgelist;
	
	private ArrayList<Heuristic> heuristiclist;

	public ArrayList<Node> getNodelist() {
		return nodelist;
	}

	public void setNodelist(ArrayList<Node> nodelist) {
		this.nodelist = nodelist;
	}

	public ArrayList<Edge> getEdgelist() {
		return edgelist;
	}

	public void setEdgelist(ArrayList<Edge> edgelist) {
		this.edgelist = edgelist;
	}

	public ArrayList<Heuristic> getHeuristiclist() {
		return heuristiclist;
	}

	public void setHeuristiclist(ArrayList<Heuristic> heuristiclist) {
		this.heuristiclist = heuristiclist;
	}

	public Node getNodeById(long _id) {
		for (int i=0; i < this.nodelist.size(); i++) {
			if (this.nodelist.get(i).getId() == _id) {
				return this.nodelist.get(i);
			}
		}
			return null;
	}
	
	public Node getNodeByName(String _name) {
		for (int i=0; i < this.nodelist.size(); i++) {
			if (this.nodelist.get(i).getName().equalsIgnoreCase(_name)) {
				return this.nodelist.get(i);
			}
		}
			return null;
	}
	
	public String[] getNodeNames(){
		String[] aux = new String[nodelist.size()];
		System.out.println("oi");
		for (int i=0; i < this.nodelist.size(); i++) {
				System.out.println("oi");
				aux[i] = this.nodelist.get(i).getName();
		}
		return aux;
	}
	
	/**
	 * Encontra a aresta entre dois v�rtices
	 * @param source O v�rtice de origem
	 * @param destiny O v�rtice de Destino
	 * @return A aresta entre os dois v�rtices ou NULL caso n�o encontre
	 */
	public Edge getEdge(Node source, Node destiny) {
		for (Edge edge : this.getEdgelist()) {
			if ((edge.getSrc() == source) && (edge.getDst() == destiny)) {
				return edge;
			}
			if ((edge.getDst() == source) && (edge.getSrc() == destiny)) {
				return edge;
			}
		}
		return null;
	}
	
	/**
	 * Encontra a heur�stica entre dois v�rtices
	 * @param source O v�rtice de origem
	 * @param destiny O v�rtice de Destino
	 * @return A heur�stica entre os dois v�rtices ou NULL caso n�o encontre
	 */
	public Heuristic getHeuristic(Node source, Node destiny) {
		if (source == destiny) {
			Heuristic result = new Heuristic();
			result.setW(0);
			return result;
		}
		for (Heuristic heuristic : this.getHeuristiclist()) {
			if ((heuristic.getSrc() == source) && (heuristic.getDst() == destiny)) {
				return heuristic;
			}
			if ((heuristic.getDst() == source) && (heuristic.getSrc() == destiny)) {
				return heuristic;
			}
			
		}
		return null;
	}
	
	/**
	 * Gera uma lista dos vizinhos de node
	 * @param node N� que ter� seus vizinhos encontrados 
	 * @return A lista de vizinhos
	 */
	public ArrayList<Node> getNeighbor(Node node) {
		ArrayList<Node> neighbor = new ArrayList<Node>();
		
		for (Edge edge : this.getEdgelist()) {
			if (edge.getSrc() == node) {
				neighbor.add(edge.getDst());
			}
			if (edge.getDst() == node) {
				neighbor.add(edge.getSrc());
			}
		}
		
		return neighbor;
	}
	
	public void downNode(Node node) {
		if (this.getNodelist().remove(node)) {
			System.out.println(" ----- Removido: " + node.getName());
			int i = 0;
			while(i < this.getEdgelist().size()) {
			//for (int i = 0; i < this.getEdgelist().size(); i++) {
				if ((this.getEdgelist().get(i).getSrc() == node) || (this.getEdgelist().get(i).getDst() == node)) {
					System.out.println(" ----- Removido Aresta: Fonte: " + this.getEdgelist().get(i).getSrc().getName() +
							"\t Destino: " + this.getEdgelist().get(i).getDst().getName());
					this.getEdgelist().remove(i);
					continue;
				}
				i++;
			}
			i = 0;
			while(i < this.getHeuristiclist().size()) {
			//for (int i = 0; i < this.getHeuristiclist().size(); i++) {
				if ((this.getHeuristiclist().get(i).getSrc() == node) || (this.getHeuristiclist().get(i).getDst() == node)) {
					this.getHeuristiclist().remove(i);
					continue;
				}
				i++;
			}
		}
	}
}
