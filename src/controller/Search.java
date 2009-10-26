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
	
	public Search(Graph graf, int metric) {
		this.grafo = graf;
		this.metrica = metric;
	}
	
	private Graph grafo;
	/**
	 * @return the grafo
	 */
	public Graph getGrafo() {
		return grafo;
	}

	/**
	 * @param grafo the grafo to set
	 */
	public void setGrafo(Graph grafo) {
		this.grafo = grafo;
	}

	/**
	 * @return the metrica
	 */
	public int getMetrica() {
		return metrica;
	}

	/**
	 * @param metrica the metrica to set
	 */
	public void setMetrica(int metrica) {
		this.metrica = metrica;
	}

	private int metrica;
	
	public void run() {
		System.out.println("UHEEET!");
		estadoDeEnlace(this.grafo, this.metrica);
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
				if (node.getSa().equals(node2.getSa()))
					node2.getPacotes().add(pkt);

			}
			
			//Criando as tabelas de roteamento para o estado de enlace
			node.setTabela(dijkstra(graph, node, metric));
		}
		
//		for (Node node : nodes) {
//			/**
//			 * Verificar se o vértice é ligação entre SAs
//			 */
//			ArrayList<Edge> vizinhos = node.getVizinhos(edges);
//			for (Edge edge : vizinhos) {
//				Node destino = (node == edge.getSrc()) ? edge.getDst() : edge.getSrc();
//				if (node.getSa().equals(destino.getSa()))
//					continue;
//				else {
//					for (Table ta : node.getTabela()) {
//						for (Table taD : destino.getTabela()) {
//							int dist = 0;
//							switch (metric) {
//							case 0: { // Distância
//								dist = taD.getCusto() + edge.getDist() + edge.getUserdist();
//							}
//							break;
//							case 1: { // Tempo de Retardo
//								dist = taD.getCusto() + edge.getRettime() + edge.getUserrettime();
//							}
//							break;
//							case 2: { // Hop Count
//								dist = taD.getCusto() + edge.getHopcount() + edge.getUserhopcount();
//							}
//							break;
//							case 3: { // Largura de Banda
//								dist = taD.getCusto() + edge.getBandwidth() + edge.getUserbandwidth();
//							}
//							}
//							if ((ta.getDestino() == taD.getDestino() && (dist < ta.getCusto()))) {
//								node.getTabela().remove(ta);
//								node.getTabela().add(new Table(taD.getDestino(),edge,dist));
//								break;
//							}
//						}
//					}
//					boolean existe;
//					for (Table taD : destino.getTabela()) {
//						existe = false;
//						for (Table ta : node.getTabela()) {
//							if (ta.getDestino() == taD.getDestino()) {
//								existe = true;
//							}
//						}
//						if (!existe) {
//							int dist = 0;
//							switch (metric) {
//							case 0: { // Distância
//								dist = taD.getCusto() + edge.getDist() + edge.getUserdist();
//							}
//							break;
//							case 1: { // Tempo de Retardo
//								dist = taD.getCusto() + edge.getRettime() + edge.getUserrettime();
//							}
//							break;
//							case 2: { // Hop Count
//								dist = taD.getCusto() + edge.getHopcount() + edge.getUserhopcount();
//							}
//							break;
//							case 3: { // Largura de Banda
//								dist = taD.getCusto() + edge.getBandwidth() + edge.getUserbandwidth();
//							}
//							}
//							node.getTabelaOutrosSA().add(new Table(taD.getDestino(),edge, dist));
//						}
//					}
//						// Cria Tabela de Roteamento para Vetor Distância
//				}
//				
//			}
//		}
		
		//DEBUG
//		for (Edge edge: getCircuito(nodes.get(1), nodes.get(2)))
//			System.out.println("Origem: " + edge.getSrc().getName() + "\tDestino: " + edge.getDst().getName());
	}
	
	public static ArrayList<Table> dijkstra(Graph graph, Node atual, int metric) {
		ArrayList<Node> nodes = graph.getNodelist();
		ArrayList<Edge> edges = graph.getEdgelist();
		
//		ArrayList<Node> nodes = new ArrayList<Node>();
//		ArrayList<Edge> edges = new ArrayList<Edge>();
//		for (Packet pack: atual.getPacotes()) {
//			if (!nodes.contains(pack.getOrigem()))
//				nodes.add(pack.getOrigem());
//		}
//		for (Packet pack: atual.getPacotes()) {
//			for (Edge ed : pack.getCaminho())
//				if (!edges.contains(ed) && nodes.contains(ed.getSrc()) && nodes.contains(ed.getDst()))
//					edges.add(ed);
//		}
		
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
				if (noded.getSa().equals(atual.getSa())) {
					NodeDist nodeDist = new NodeDist(noded, Integer.MAX_VALUE);
					distancia.add(nodeDist);
					listaVertices.add(nodeDist);
				}
			}
			noded.setFather(null);
		}
		
		while(listaVertices.size() > 0) {
			Collections.sort(listaVertices);
			NodeDist menorDist = listaVertices.remove(0);
			if (menorDist.getDistancia() == Integer.MAX_VALUE)
				break;
			for (Edge aresta: menorDist.getNode().getVizinhos(edges)) {
				int dist = 0;
				switch (metric) {
				case 0: { // Distância
					dist = menorDist.getDistancia() + aresta.getDist();
				}
				break;
				case 1: { // Tempo de Retardo
					dist = menorDist.getDistancia() + aresta.getRettime();
				}
				break;
				case 2: { // Hop Count
					dist = menorDist.getDistancia() + aresta.getHopcount();
				}
				break;
				case 3: { // Largura de Banda
					dist = menorDist.getDistancia() + aresta.getBandwidth();
				}
				}
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
		
		/**
		 * Montando tabela de roteamento
		 */
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
