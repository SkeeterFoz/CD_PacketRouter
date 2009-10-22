package controller;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class IOFile {
	
	private String path;
	
	private String line;
	
	private int nl;
	
	private FileReader f;
	
	private BufferedReader in;
	
	private String[] vlines = new String[500];
	
	//M�todo que verifica a exist�ncia e se n�o existir mostra uma msg na tela
	public IOFile(String _path) throws FileNotFoundException {
		this.path = _path;
		try {
			f = new FileReader(this.path);			
		} catch(FileNotFoundException e) {
			throw new FileNotFoundException("Arquivo n�o encontrado");
		}
	}
	
	//M�todo que l� o arquivo e guarda num vetor de linhas
	public void Read() throws IOException {
		this.nl = 0;
		this.in = new BufferedReader(f);
		
		try {
			line = in.readLine();
			while(line != null) {
				vlines[nl] = line;
				line = in.readLine();
				nl++;				
			}
		} catch(IOException e) {
			throw(new IOException("Falha de I/O no arquivo"));
		}
	
	}
	
	public int getN() {
		return this.nl;
	}
	
	public String[] getV() {
		return this.vlines;
	}

}
