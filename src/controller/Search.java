package controller;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

import org.eclipse.swt.internal.cde.DtActionArg;

import model.Edge;
import model.Heuristic;
import model.Node;

public class Search {
	/**
	 * Função que encontra o menor caminho entre dois nós com ajuda de uma heurística
	 * @param source Nó de origem
	 * @param destiny Nó destino
	 * @param graph Grafo contendo todas as arestas e heurísticas
	 * @return Retorna a lista de vértices pertencentes ao caminho ou uma lista vazia caso o caminho não exista
	 */
	public static ArrayList<Edge> AStar(Node source, Node destiny, Graph graph, int metric) {
		System.out.println("começo " + (new Date()).getTime());
		ArrayList<Node> nodes = graph.getNodelist();
		ArrayList<Node> open = new ArrayList<Node>();
		ArrayList<Node> close = new ArrayList<Node>();
		ArrayList<Edge> path = new ArrayList<Edge>();
		Node currentNode;		
		
		System.out.println("Chegou  Fonte: " + source.getName() + "  Destino: " + destiny.getName());
		
		for (Node node : nodes) {
			node.setCost(0);
			node.setHeuristic(0);
			node.setFather(null);
			node.setChild(new ArrayList<Node>());
		}
		
		open.add(source);
		while(open.size() > 0) {
			currentNode = open.remove(0);
			System.out.println("Currente -> " + currentNode.getName());
			if (currentNode == destiny) {
				System.out.println("Achou o destino");
				Node aux = currentNode.getFather();
				while ((currentNode != source) && (aux != null)) {
					System.out.println("Currente: " + currentNode.getName());
					System.out.println("father: " + aux.getName());
					path.add(graph.getEdge(currentNode, aux));
					currentNode = aux;
					aux = currentNode.getFather();
					System.out.println("Final Currente: " + currentNode.getName());
					//System.out.println("Final father: " + aux.getName());
				}
				System.out.println("fim " + (new Date()).getTime());
				return path;
			}
			close.add(currentNode);
			ArrayList<Node> neighbor = graph.getNeighbor(currentNode);
			for (int j = 0; j < neighbor.size(); j++) {
				Node node = neighbor.get(j);
				if (!node.isState())
					continue;
				System.out.println("Vizinho Atual: " + node.getName());
				System.out.println();
				if (!Search.updateCost(currentNode, node, graph, metric, open, close)) {
					Edge edge = graph.getEdge(currentNode, node);
					switch (metric) {
					case 0: { // Distância
						if (edge.isEqualsa()) {
							node.setCost(currentNode.getCost() + edge.getDist());	
						} else {
							node.setCost(currentNode.getCost() + edge.getDist() + edge.getUserdist());
						}						
						node.setHeuristic(graph.getHeuristic(node, destiny).getW());
						node.setFather(currentNode);
						if (open.size() == 0) {
							open.add(node);
							break;
						}
						for (int i = 0; i < open.size(); i++) {
							if ((node.getCost() + node.getHeuristic()) < 
									(open.get(i).getCost() + open.get(i).getHeuristic())) {
								open.add(i, node);
								System.out.println("vizinho adiconado no open posição: " + i);
								break;
							}
							if (i == (open.size() - 1)) {
								open.add(node);
								System.out.println("vizinho adiconado no open posição: " + i);
								break;
							}
						}
					}
					break;
					case 1: { // Tempo de Retardo
						if (edge.isEqualsa()) {
							node.setCost(currentNode.getCost() + edge.getRettime());	
						} else {
							node.setCost(currentNode.getCost() + edge.getRettime() + edge.getUserrettime());
						}
						node.setHeuristic(graph.getHeuristic(node, destiny).getW());
						node.setFather(currentNode);
						if (open.size() == 0) {
							open.add(node);
							break;
						}
						for (int i = 0; i < open.size(); i++) {
							if ((node.getCost() + node.getHeuristic()) < 
									(open.get(i).getCost() + open.get(i).getHeuristic())) {
								open.add(i, node);
								break;
							}
							if (i == (open.size() - 1)) {
								open.add(node);
								break;
							}
						}
					}
					break;
					case 2: { // Hop Count
						if (edge.isEqualsa()) {
							node.setCost(currentNode.getCost() + edge.getHopcount());	
						} else {
							node.setCost(currentNode.getCost() + edge.getHopcount() + edge.getUserhopcount());
						}
						node.setHeuristic(graph.getHeuristic(node, destiny).getW());
						node.setFather(currentNode);
						if (open.size() == 0) {
							open.add(node);
							break;
						}
						for (int i = 0; i < open.size(); i++) {
							if ((node.getCost() + node.getHeuristic()) < 
									(open.get(i).getCost() + open.get(i).getHeuristic())) {
								open.add(i, node);
								break;
							}
							if (i == (open.size() - 1)) {
								open.add(node);
								break;
							}
						}
					}
					break;
					case 3: { // Largura de Banda
						if (edge.isEqualsa()) {
							node.setCost(currentNode.getCost() + edge.getBandwidth());	
						} else {
							node.setCost(currentNode.getCost() + edge.getBandwidth() + edge.getUserbandwidth());
						}
						node.setHeuristic(graph.getHeuristic(node, destiny).getW());
						node.setFather(currentNode);
						if (open.size() == 0) {
							open.add(node);
							break;
						}
						for (int i = 0; i < open.size(); i++) {
							if ((node.getCost() + node.getHeuristic()) > 
									(open.get(i).getCost() + open.get(i).getHeuristic())) {
								open.add(i, node);
								break;
							}
							if (i == (open.size() - 1)) {
								open.add(node);
								break;
							}
						}
					}
					}
				}
				
			}
		}
		System.out.println("fim " + (new Date()).getTime());
		return path;
	}
	
	/**
	 * Função que atualiza o custo de um nó caso ache um outro caminho melhor até este nó
	 * @param currentNode Nó atual
	 * @param node Vértice que poderá ter seu custo alterado
	 * @param graph Grafo contendo todas as informações de todos os vértices e arestas
	 * @param metric A métrica utilizada para decidir se o novo caminho é melhor
	 * @param open Lista de vértices que se encontram abertos mas não verificados
	 * @param close Lista de vértices já verificados
	 * @return True caso node se encontre na lista de abertos ou fechados, False caso contrário
	 */
	static boolean updateCost(Node currentNode, Node node, Graph graph, int metric, 
								ArrayList<Node> open, ArrayList<Node> close) {
		if (open.contains(node)) {
			currentNode.getChild().add(node);
			System.out.println("Atualização Open   Currente = " + currentNode.getName());
			System.out.println("Node = " + node.getName());
			Edge edge = graph.getEdge(currentNode, node);
			switch (metric) {
			case 0: {// Distância
				long function;
				if (edge.isEqualsa()) {
					function = currentNode.getCost() + edge.getDist();	
				} else {
					function = currentNode.getCost() + edge.getDist() + edge.getUserdist();
				}
				if (node.getCost() > function) {
					node.setCost(function);
					node.setFather(currentNode);
					System.out.println("Novo valor de node = " + node.getCost());
				}
			}
			break;
			case 1: { // Tempo de Retardo
				long function;
				if (edge.isEqualsa()) {
					function = currentNode.getCost() + edge.getRettime();	
				} else {
					function = currentNode.getCost() + edge.getRettime() + edge.getUserrettime();
				}
				if (node.getCost() > function) {
					node.setCost(function);
					node.setFather(currentNode);
				}
			}
			break;
			case 2: { // Hop Count
				long function;
				if (node.getSa() == currentNode.getSa()) {
					function = currentNode.getCost() + edge.getHopcount();	
				} else {
					function = currentNode.getCost() + edge.getHopcount() + edge.getUserhopcount();
				}
				if (node.getCost() > function) {
					node.setCost(function);
					node.setFather(currentNode);
				}
			}
			break;
			case 3: { // Largura de Banda
				long function;
				if (node.getSa() == currentNode.getSa()) {
					function = currentNode.getCost() + edge.getBandwidth();	
				} else {
					function = currentNode.getCost() + edge.getBandwidth() + edge.getUserbandwidth();
				}
				if (node.getCost() < function) {
					node.setCost(function);
					node.setFather(currentNode);
				}
			}
			}
			return true;
		} else if (close.contains(node)) {
			currentNode.getChild().add(node);
			System.out.println("Atualização Close   Currente = " + currentNode.getName());
			System.out.println("Node = " + node.getName());
			Edge edge = graph.getEdge(currentNode, node);
			switch (metric) {
			case 0: {// Distância
				long function;
				if (edge.isEqualsa()) {
					function = currentNode.getCost() + edge.getDist();	
				} else {
					function = currentNode.getCost() + edge.getDist() + edge.getUserdist();
				}
				if (node.getCost() > function) {
					node.setCost(function);
					node.setFather(currentNode);
					System.out.println("Novo valor de node = " + node.getCost());
					for (Node child : node.getChild()) {
						updateCost(node, child, graph, metric, open, close);					
					}
				}
			}
			break;
			case 1: { // Tempo de Retardo
				long function;
				if (edge.isEqualsa()) {
					function = currentNode.getCost() + edge.getRettime();	
				} else {
					function = currentNode.getCost() + edge.getRettime() + edge.getUserrettime();
				}
				if (node.getCost() > function) {
					node.setCost(function);
					node.setFather(currentNode);
					for (Node child : node.getChild()) {
						updateCost(node, child, graph, metric, open, close);					
					}
				}
			}
			break;
			case 2: { // Hop Count
				long function;
				if (edge.isEqualsa()) {
					function = currentNode.getCost() + edge.getHopcount();	
				} else {
					function = currentNode.getCost() + edge.getHopcount() + edge.getUserhopcount();
				}
				if (node.getCost() > function) {
					node.setCost(function);
					node.setFather(currentNode);
					for (Node child : node.getChild()) {
						updateCost(node, child, graph, metric, open, close);					
					}
				}
			}
			break;
			case 3: { // Largura de Banda
				long function;
				if (edge.isEqualsa()) {
					function = currentNode.getCost() + edge.getBandwidth();	
				} else {
					function = currentNode.getCost() + edge.getBandwidth() + edge.getUserbandwidth();
				}
				if (node.getCost() < function) {
					node.setCost(function);
					node.setFather(currentNode);
					for (Node child : node.getChild()) {
						updateCost(node, child, graph, metric, open, close);					
					}
				}
			}
			}
			return true;
		}	
		return false;
	}
	
	/**
	 * Função que encontra o menor caminho entre dois nós
	 * @param source Nó de origem
	 * @param destiny Nó destino
	 * @param graph Grafo contendo todas as arestas e heurísticas
	 * @return Retorna a lista de vértices pertencentes ao caminho ou uma lista vazia caso o caminho não exista
	 */
	public static ArrayList<Edge> width(Node source, Node destiny, Graph graph, int metric) {
		System.out.println("começo " + (new Date()).getTime());
		ArrayList<Node> nodes = graph.getNodelist();
		ArrayList<Node> open = new ArrayList<Node>();
		ArrayList<Node> close = new ArrayList<Node>();
		ArrayList<Edge> path = new ArrayList<Edge>();
		Node currentNode;		
		
		System.out.println("Chegou  Fonte: " + source.getName() + "  Destino: " + destiny.getName());
		
		for (Node node : nodes) {
			node.setCost(0);
			node.setFather(null);
			node.setChild(new ArrayList<Node>());
		}
		
		open.add(source);
		while(open.size() > 0) {
			currentNode = open.remove(0);
			System.out.println("Currente -> " + currentNode.getName());
			if (currentNode == destiny) {
				System.out.println("Achou o destino");
				Node aux = currentNode.getFather();
				while ((currentNode != source) && (aux != null)) {
					System.out.println("Currente: " + currentNode.getName());
					System.out.println("father: " + aux.getName());
					path.add(graph.getEdge(currentNode, aux));
					currentNode = aux;
					aux = currentNode.getFather();
					System.out.println("Final Currente: " + currentNode.getName());
					//System.out.println("Final father: " + aux.getName());
				}
				System.out.println("fim " + (new Date()).getTime());
				return path;
			}
			close.add(currentNode);
			ArrayList<Node> neighbor = graph.getNeighbor(currentNode);
			for (int j = 0; j < neighbor.size(); j++) {
				Node node = neighbor.get(j);
				if (!node.isState())
					continue;
				System.out.println("Vizinho Atual: " + node.getName());
				System.out.println();
				if (!Search.updateCostWidth(currentNode, node, graph, metric, open, close)) {
					Edge edge = graph.getEdge(currentNode, node);
					switch (metric) {
					case 0: { // Distância
						if (edge.isEqualsa()) {
							node.setCost(currentNode.getCost() + edge.getDist());	
						} else {
							node.setCost(currentNode.getCost() + edge.getDist() + edge.getUserdist());
						}
						node.setFather(currentNode);
						open.add(node);
					}
					break;
					case 1: { // Tempo de Retardo
						if (edge.isEqualsa()) {
							node.setCost(currentNode.getCost() + edge.getRettime());	
						} else {
							node.setCost(currentNode.getCost() + edge.getRettime() + edge.getUserrettime());
						}
						node.setFather(currentNode);
						open.add(node);
					}
					break;
					case 2: { // Hop Count
						if (edge.isEqualsa()) {
							node.setCost(currentNode.getCost() + edge.getHopcount());	
						} else {
							node.setCost(currentNode.getCost() + edge.getHopcount() + edge.getUserhopcount());
						}
						node.setFather(currentNode);
						open.add(node);
					}
					break;
					case 3: { // Largura de Banda
						if (edge.isEqualsa()) {
							node.setCost(currentNode.getCost() + edge.getBandwidth());	
						} else {
							node.setCost(currentNode.getCost() + edge.getBandwidth() + edge.getUserbandwidth());
						}
						node.setFather(currentNode);
						open.add(node);
					}
					}
				}
				
			}
		}
		System.out.println("fim " + (new Date()).getTime());
		return path;
	}
	
	/**
	 * Função que atualiza o custo de um nó caso ache um outro caminho melhor até este nó
	 * @param currentNode Nó atual
	 * @param node Vértice que poderá ter seu custo alterado
	 * @param graph Grafo contendo todas as informações de todos os vértices e arestas
	 * @param metric A métrica utilizada para decidir se o novo caminho é melhor
	 * @param open Lista de vértices que se encontram abertos mas não verificados
	 * @param close Lista de vértices já verificados
	 * @return True caso node se encontre na lista de abertos ou fechados, False caso contrário
	 */
	static boolean updateCostWidth(Node currentNode, Node node, Graph graph, int metric, 
								ArrayList<Node> open, ArrayList<Node> close) {
		if (open.contains(node)) {
			if (currentNode.getChild().contains(node))
				return true;
			currentNode.getChild().add(node);
			System.out.println("Atualização Open   Currente = " + currentNode.getName());
			System.out.println("Node = " + node.getName());
			Edge edge = graph.getEdge(currentNode, node);
			switch (metric) {
			case 0: {// Distância
				long function;
				if (edge.isEqualsa()) {
					function = currentNode.getCost() + edge.getDist();	
				} else {
					function = currentNode.getCost() + edge.getDist() + edge.getUserdist();
				}
				if (node.getCost() > function) {
					node.setCost(function);
					node.setFather(currentNode);
					System.out.println("Novo valor de node = " + node.getCost());
				}
			}
			break;
			case 1: { // Tempo de Retardo
				long function;
				if (edge.isEqualsa()) {
					function = currentNode.getCost() + edge.getRettime();	
				} else {
					function = currentNode.getCost() + edge.getRettime() + edge.getUserrettime();
				}
				if (node.getCost() > function) {
					node.setCost(function);
					node.setFather(currentNode);
				}
			}
			break;
			case 2: { // Hop Count
				long function;
				if (node.getSa() == currentNode.getSa()) {
					function = currentNode.getCost() + edge.getHopcount();	
				} else {
					function = currentNode.getCost() + edge.getHopcount() + edge.getUserhopcount();
				}
				if (node.getCost() > function) {
					node.setCost(function);
					node.setFather(currentNode);
				}
			}
			break;
			case 3: { // Largura de Banda
				long function;
				if (node.getSa() == currentNode.getSa()) {
					function = currentNode.getCost() + edge.getBandwidth();	
				} else {
					function = currentNode.getCost() + edge.getBandwidth() + edge.getUserbandwidth();
				}
				if (node.getCost() < function) {
					node.setCost(function);
					node.setFather(currentNode);
				}
			}
			}
			return true;
		} else if (close.contains(node)) {
			if (currentNode.getChild().contains(node))
				return true;
			currentNode.getChild().add(node);
			System.out.println("Atualização Close   Currente = " + currentNode.getName());
			System.out.println("Node = " + node.getName());
			Edge edge = graph.getEdge(currentNode, node);
			switch (metric) {
			case 0: {// Distância
				long function;
				if (edge.isEqualsa()) {
					function = currentNode.getCost() + edge.getDist();	
				} else {
					function = currentNode.getCost() + edge.getDist() + edge.getUserdist();
				}
				if (node.getCost() > function) {
					node.setCost(function);
					node.setFather(currentNode);
					System.out.println("Novo valor de node = " + node.getCost());
					ArrayList<Node> chil = node.getChild();
					for (Node child : chil) {
						updateCostWidth(node, child, graph, metric, open, close);					
					}
				}
			}
			break;
			case 1: { // Tempo de Retardo
				long function;
				if (edge.isEqualsa()) {
					function = currentNode.getCost() + edge.getRettime();	
				} else {
					function = currentNode.getCost() + edge.getRettime() + edge.getUserrettime();
				}
				if (node.getCost() > function) {
					node.setCost(function);
					node.setFather(currentNode);
					for (Node child : node.getChild()) {
						updateCostWidth(node, child, graph, metric, open, close);					
					}
				}
			}
			break;
			case 2: { // Hop Count
				long function;
				if (edge.isEqualsa()) {
					function = currentNode.getCost() + edge.getHopcount();	
				} else {
					function = currentNode.getCost() + edge.getHopcount() + edge.getUserhopcount();
				}
				if (node.getCost() > function) {
					node.setCost(function);
					node.setFather(currentNode);
					for (Node child : node.getChild()) {
						updateCostWidth(node, child, graph, metric, open, close);					
					}
				}
			}
			break;
			case 3: { // Largura de Banda
				long function;
				if (edge.isEqualsa()) {
					function = currentNode.getCost() + edge.getBandwidth();	
				} else {
					function = currentNode.getCost() + edge.getBandwidth() + edge.getUserbandwidth();
				}
				if (node.getCost() < function) {
					node.setCost(function);
					node.setFather(currentNode);
					for (Node child : node.getChild()) {
						updateCostWidth(node, child, graph, metric, open, close);					
					}
				}
			}
			}
			return true;
		}	
		return false;
	}
}
