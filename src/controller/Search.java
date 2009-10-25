package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.TimerTask;

import model.Edge;
import model.Node;
import model.NodeDist;
import model.Packet;
import model.Table;

public class Search extends TimerTask {
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
	
	public void run() {
		System.out.println("UHEEET!");
	}
	
	public static void estadoDeEnlace(Graph graph, int metric) 
	{
		ArrayList<Node> nodes = graph.getNodelist();
		ArrayList<Edge> edges = graph.getEdgelist();
		
		for (Node node : nodes) {
			
			/**
			 * Criação de pacote
			 */
			Packet pkt = new Packet();
			pkt.setOrigem(node);
			pkt.setIdade(0);
			
			/**
			 * Preenchendo com vizinhos e seus tempos
			 */
			ArrayList<Edge> tmp = new ArrayList<Edge>();
			for (Edge edge : edges) {
				if (node == edge.getSrc() || node == edge.getDst())
					tmp.add(edge);
			}
			pkt.setCaminho(tmp);

//			DEBUG			
//			System.out.println("origem: " + pkt.getOrigem().getName());
//			System.out.println("idade: " + pkt.getIdade());
//			for (Edge edge : pkt.getCaminho())
//				System.out.println("caminho: "+ edge.getSrc().getName() + " " + edge.getDst().getName());
			
			/**
			 * Enviar pacote para todos os outros roteadores
			 */
			for (Node node2 : nodes) {
				// Não enviar pra si mesmo
				if (node == node2) 
					continue;
				
				node2.getPacotes().add(pkt);

			}
			
			
			
		}
		
		/**
		 * Dijkstra
		 */
		
		for (Node node : nodes) {
			node.setTabela(dijkstra(graph, node));
		}
		for (Edge edge: getCircuito(nodes.get(1), nodes.get(2)))
			System.out.println("Origem: " + edge.getSrc().getName() + "\tDestino: " + edge.getDst().getName());
	}
	
	public static ArrayList<Table> dijkstra(Graph graph, Node atual) {
		ArrayList<Node> nodes = graph.getNodelist();
		ArrayList<Edge> edges = graph.getEdgelist();
		
		ArrayList<NodeDist> distancia = new ArrayList<NodeDist>();
		ArrayList<NodeDist> listaVertices = new ArrayList<NodeDist>();
		ArrayList<Table> tabela = new ArrayList<Table>();
		
		//Inicialização
		for (Node noded : nodes) {
			if (noded.equals(atual)) {
				NodeDist nodeDist = new NodeDist(noded, 0); 
				distancia.add(0, nodeDist);
				listaVertices.add(0, nodeDist);
			} else {
				NodeDist nodeDist = new NodeDist(noded, Integer.MAX_VALUE);
				distancia.add(nodeDist);
				listaVertices.add(nodeDist);
			}
			noded.setFather(null);
		}
		
		while(listaVertices.size() > 0) {
			Collections.sort(listaVertices);
			NodeDist menorDist = listaVertices.remove(0);
			if (menorDist.getDistancia() == Integer.MAX_VALUE)
				break;
			for (Edge aresta: menorDist.getNode().getVizinhos(edges)) {
				int dist = menorDist.getDistancia() + aresta.getDist();
				if (menorDist.getNode() == aresta.getDst()) {
					for (NodeDist nodeD: distancia) {
						if (nodeD.getNode() == aresta.getSrc()) {
							if (dist < nodeD.getDistancia()) {
								nodeD.setDistancia(dist);
								nodeD.getNode().setFather(menorDist.getNode());
							}
							break;
						}
							
					}
				} else {
					for (NodeDist nodeD: distancia) {
						if (nodeD.getNode() == aresta.getDst()) {
							if (dist < nodeD.getDistancia()) {
								nodeD.setDistancia(dist);
								nodeD.getNode().setFather(menorDist.getNode());
							}
							break;
						}	
					}
				}
			}
		}
		
//		DEBUG			
		System.out.println("Distancia apos calculo");
		for (NodeDist no : distancia) {
			System.out.print("No: "+ no.getNode().getName() + "\tDistancia: " + no.getDistancia());
			if (no.getNode().getFather() != null) {
				System.out.println("\tPai: " + no.getNode().getFather().getName());
			} else {
				System.out.println("\t Sem pai");
			}
		}
		
		for (NodeDist nodeD: distancia) {
			//Se não for o nó de origem nem um inalcansável
			if ((nodeD.getDistancia() > 0) && (nodeD.getDistancia() < Integer.MAX_VALUE)) {
				Node paiAtual = nodeD.getNode().getFather();
				Node paiAux = nodeD.getNode();
				while (paiAtual != atual) {
					paiAux = paiAtual;
					paiAtual = paiAtual.getFather();
				}
				Edge aresta = null;
//				System.out.println("No: " + nodeD.getNode().getName() + "\tPaiAux: " + paiAux.getName());
				for (Edge edge : edges) {
					if (((edge.getSrc() == atual) && (edge.getDst() == paiAux)) || 
							((edge.getSrc() == paiAux) && (edge.getDst() == atual))) {
						aresta = edge;
//						System.out.println("Origem: " + edge.getSrc().getName() + 
//								"\tDestino: " + edge.getDst().getName());
					}
				}
				tabela.add(new Table(nodeD.getNode(), aresta, nodeD.getDistancia()));
			} else if (nodeD.getDistancia() == 0) { //Nó origem
				tabela.add(new Table(nodeD.getNode(), null, 0));
			} else { //Nó Inalcansável
				tabela.add(new Table(nodeD.getNode(), null, Integer.MAX_VALUE));
			}
							
		}
		
//		DEBUG			
		System.out.println("Tabela de Roteamento do " + atual.getName());
		for (Table tab : tabela) {
			tab.print();
		}
		
		return tabela;
	}
	
	public static ArrayList<Edge> getCircuito(Node origem, Node destino) {
		ArrayList<Edge> circuito = new ArrayList<Edge>();
		Node aux = origem;
		while (aux != destino) {
			for (Table tabela: aux.getTabela()) {
				if (tabela.getDestino() == destino) {
					circuito.add(tabela.getLinha());
					if (tabela.getLinha().getSrc() == aux) {
						aux = tabela.getLinha().getDst();
					} else {
						aux = tabela.getLinha().getSrc();
					}
				}
			}
		}
		
		return circuito;
	}
}
