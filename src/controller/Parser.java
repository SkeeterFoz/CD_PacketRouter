package controller;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import model.Edge;
import model.Heuristic;
import model.Node;

public class Parser {

	public Parser() {
			
	}
	
	public void TextToGraph(String _path, Graph g) throws Exception {
		/** Vetores temp */
		Vector<String> vn, ve, vh;
		ArrayList<Node> an;
		ArrayList<Edge> ae;
		ArrayList<Heuristic> ah;
		
		String[] buf = new String[500];
		
		try {
			IOFile io = new IOFile(_path);
			io.Read();
			
			buf = io.getV();
			
			vn = new Vector<String>();
			ve = new Vector<String>();
			vh = new Vector<String>();
			
			int i;
			for(i=0; i < io.getN(); i++) {
				
				if (buf[i].contains("/*"))
					continue;
			
				if (buf[i].contains("ligacao(")) {
					ve.add(buf[i]);
					continue;
				}
			
				if (buf[i].contains("caracteristica(")) {
					vn.add(buf[i]);
					continue;
				}
				
				if (buf[i].contains("h(")) {
					vh.add(buf[i]);
					continue;
				}
			}
			
			int j;
			StringTokenizer strtok;
			
			an = new ArrayList<Node>();
			ae = new ArrayList<Edge>();
			ah = new ArrayList<Heuristic>();
			
			for (j=0; j < vn.size(); j++) {
				strtok = new StringTokenizer(vn.elementAt(j), "(");
				strtok.nextToken();
				strtok = new StringTokenizer(strtok.nextToken(), ")");
				strtok = new StringTokenizer(strtok.nextToken(), ",");
				
				Node n = new Node();
				n.setId(j);
				n.setName(strtok.nextToken().trim());
				n.setState(strtok.nextToken().trim().equalsIgnoreCase("up") ? true : false);
				n.setSa(strtok.nextToken().trim());
				
				an.add(n);
				
			}
			
			g.setNodelist(an);		

			for (j=0; j < ve.size(); j++) {
				strtok = new StringTokenizer(ve.elementAt(j), "(");
				strtok.nextToken();
				strtok = new StringTokenizer(strtok.nextToken(), ")");
				strtok = new StringTokenizer(strtok.nextToken(), ",");
				
				
				Edge e = new Edge();
				Node src = new Node();
				Node dst = new Node();
				String m1, um1, m2, um2, m3, um3, m4, um4;
				
				src = g.getNodeByName(strtok.nextToken().trim());
				dst = g.getNodeByName(strtok.nextToken().trim());

				
				if ((src == null) || (dst == null)) {
					throw new Exception("Aresta inválida");
				}
				
				e.setId(j);
				e.setSrc(src);
				e.setDst(dst);
				
				m1 = strtok.nextToken();
				um1 = strtok.nextToken();
				m2 = strtok.nextToken();
				um2 = strtok.nextToken();
				m3 = strtok.nextToken();
				um3 = strtok.nextToken();
				m4 = strtok.nextToken();
				um4 = strtok.nextToken();
				
				strtok = new StringTokenizer(m1, "[");
				strtok.nextToken();
				int dist = Integer.parseInt(strtok.nextToken().trim());
				e.setDist(dist);				
				strtok = new StringTokenizer(um1, "]");
				e.setUserdist(Integer.parseInt(strtok.nextToken().trim()) + dist);
				
				strtok = new StringTokenizer(m2, "[");
				strtok.nextToken();
				int ret = Integer.parseInt(strtok.nextToken().trim());
				e.setRettime(ret);				
				strtok = new StringTokenizer(um2, "]");
				e.setUserrettime(Integer.parseInt(strtok.nextToken().trim()) + ret);
				
				strtok = new StringTokenizer(m3, "[");
				strtok.nextToken();
				int band = Integer.parseInt(strtok.nextToken().trim());
				e.setBandwidth(band);				
				strtok = new StringTokenizer(um3, "]");
				e.setUserbandwidth(Integer.parseInt(strtok.nextToken().trim()) + band);

				strtok = new StringTokenizer(m4, "[");
				strtok.nextToken();
				int hop = Integer.parseInt(strtok.nextToken().trim());
				e.setHopcount(hop);				
				strtok = new StringTokenizer(um4, "]");
				e.setUserhopcount(Integer.parseInt(strtok.nextToken().trim()) + hop);
				
				e.setEqualsa(e.getSrc().getSa().equalsIgnoreCase(e.getDst().getSa()));
				
				ae.add(e);
				
			}
			
			g.setEdgelist(ae);
			
			for (j=0; j < vh.size(); j++) {
				strtok = new StringTokenizer(vh.elementAt(j), "(");
				strtok.nextToken();
				strtok = new StringTokenizer(strtok.nextToken(), ")");
				strtok = new StringTokenizer(strtok.nextToken(), ",");
				
				
				Heuristic h = new Heuristic();
				Node src = new Node();
				Node dst = new Node();
				
				src = g.getNodeByName(strtok.nextToken().trim());
				dst = g.getNodeByName(strtok.nextToken().trim());
				
				if ((src == null) || (dst == null)) {
					throw new Exception("Aresta inválida");
				}
				
				h.setSrc(src);
				h.setDst(dst);
				h.setW(Integer.parseInt(strtok.nextToken().trim()));
				
				ah.add(h);
				
			}
			
			g.setHeuristiclist(ah);
			
			
			
		} catch(FileNotFoundException e1) {
			throw e1;
		} catch(IOException e2) {
			throw e2;
		}
	}
	
	public void GraphToXml(Graph g, ArrayList<Edge> _pathlist, int _metrica, String _path) throws IOException {
		
		StringBuffer xml = new StringBuffer(
		"<?xml version=\"1.0\" encoding=\"ISO8859-1\"?>\n<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\">\n" + 
		"\t<graph edgedefault=\"undirected\">\n" +
    	"\t<key id=\"name\" for=\"node\" attr.name=\"name\" attr.type=\"string\"/>\n" +
    	"\t<key id=\"state\" for=\"node\" attr.name=\"state\" attr.type=\"string\"/>\n" +
    	"\t<key id=\"sa\" for=\"node\" attr.name=\"sa\" attr.type=\"string\"/>\n" +
    	"\t<key id=\"weight\" for=\"edge\" attr.name=\"weight\" attr.type=\"string\"/>\n" +
    	"\t<key id=\"waycolor\" for=\"edge\" attr.name=\"waycolor\" attr.type=\"string\"/>\n"
    	);
		
		for (int i=0; i < g.getNodelist().size(); i++) {
			
			xml.append(
			"\n\t\t<node id=\"" + i + "\">" +
			"\n\t\t\t<data key=\"name\">" + g.getNodelist().get(i).getName() + "</data>" + 
            "\n\t\t\t<data key=\"state\">" + ((g.getNodelist().get(i).isState()) ? "true" : "false") + "</data>" + 
            "\n\t\t\t<data key=\"sa\">" + g.getNodelist().get(i).getSa() + "</data>" + 
            "\n\t\t</node>");
		}
		
		for (int i=0; i < g.getEdgelist().size(); i++) {
			xml.append(
			"\n\n\t\t<edge source=\"" + g.getEdgelist().get(i).getSrc().getId() + "\" target=\"" + g.getEdgelist().get(i).getDst().getId() + "\">" +
			"\n\t\t\t<data key=\"weight\">");
			System.out.println("metrica: " + _metrica);
			switch (_metrica) {
			case 0: xml.append( ((g.getEdgelist().get(i).isEqualsa()) ? g.getEdgelist().get(i).getDist() : g.getEdgelist().get(i).getUserdist())); break;
			case 1: xml.append( ((g.getEdgelist().get(i).isEqualsa()) ? g.getEdgelist().get(i).getRettime() : g.getEdgelist().get(i).getUserrettime())); break;
			case 2: xml.append( ((g.getEdgelist().get(i).isEqualsa()) ? g.getEdgelist().get(i).getHopcount() : g.getEdgelist().get(i).getUserhopcount())); break;
			case 3: xml.append( ((g.getEdgelist().get(i).isEqualsa()) ? g.getEdgelist().get(i).getBandwidth() : g.getEdgelist().get(i).getUserbandwidth())); break;
			}
			
			xml.append("</data>");
			xml.append("\n\t\t\t<data key=\"waycolor\">");
			if (_pathlist != null) {
				if (ConstainsPath(_pathlist, g.getEdgelist().get(i).getSrc().getId(), g.getEdgelist().get(i).getDst().getId())) {
					xml.append("color3</data>\n\n\t</edge>");
					continue;
				}
			}
			
			xml.append((((g.getEdgelist().get(i).getSrc().isState()) && (g.getEdgelist().get(i).getDst().isState())) ? "color1" : "color2") + "</data>");

			xml.append("\n\t\t</edge>");
		}
		
		xml.append("\n\n\t</graph>\n</graphml>");
		
		BufferedWriter out = new BufferedWriter(new FileWriter(_path));
        out.write(xml.toString());
        out.flush();
        out.close();
	
	}

	private boolean ConstainsPath(ArrayList<Edge> path, long src, long dst)
	{
		for (int i=0; i < path.size(); i++)
			if ((path.get(i).getSrc().getId() == src) && (path.get(i).getDst().getId() == dst))
				return true;
		return false;
	}
		
}

