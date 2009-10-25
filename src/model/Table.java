package model;

public class Table {
	
	public Table(Node dest, Edge li, int cust) {
		this.setDestino(dest);
		this.setLinha(li);
		this.setCusto(cust);
	}
	
	private Node destino;
	
	private Edge linha;
	
	private int custo;
	
	public Node getDestino() {
		return destino;
	}

	public void setDestino(Node destino) {
		this.destino = destino;
	}

	public Edge getLinha() {
		return linha;
	}

	public void setLinha(Edge linha) {
		this.linha = linha;
	}

	public int getCusto() {
		return custo;
	}

	public void setCusto(int custo) {
		this.custo = custo;
	}
	
	public void print() {
		System.out.print("Destino: " + destino.getName() + "\tCusto: " + this.getCusto());
		if (this.linha != null) {
				System.out.println("\tArestaS: " + linha.getSrc().getName() + "\tArestaD: " + linha.getDst().getName());
		} else {
			System.out.println("\t Aresta não encontrada");
		}
	}
	
}
