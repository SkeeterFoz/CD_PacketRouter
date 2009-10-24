package model;

import java.util.ArrayList;

public class Packet {
	
	private Node origem;
	
	@SuppressWarnings("unused")
	private static int sequencia;
	
	private int idade;
	
	private ArrayList<Edge> caminho;

	public Node getOrigem() {
		return origem;
	}

	public void setOrigem(Node origem) {
		this.origem = origem;
	}

	public int getIdade() {
		return idade;
	}

	public void setIdade(int idade) {
		this.idade = idade;
	}

	public ArrayList<Edge> getCaminho() {
		return caminho;
	}

	public void setCaminho(ArrayList<Edge> caminho) {
		this.caminho = caminho;
	}
	
	

}
