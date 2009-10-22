package model;

public class Edge {
	
	private long id;
	
	private Node src;
	
	private Node dst;
	
	private int dist;
	
	private int userdist;
	
	private int rettime;
	
	private int userrettime;
	
	private int bandwidth;
	
	private int userbandwidth;
	
	private int hopcount;
	
	private int userhopcount;
	
	private boolean equalsa;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Node getSrc() {
		return src;
	}

	public void setSrc(Node src) {
		this.src = src;
	}

	public Node getDst() {
		return dst;
	}

	public void setDst(Node dst) {
		this.dst = dst;
	}

	public int getDist() {
		return dist;
	}

	public void setDist(int dist) {
		this.dist = dist;
	}

	public int getUserdist() {
		return userdist;
	}

	public void setUserdist(int userdist) {
		this.userdist = userdist;
	}

	public int getRettime() {
		return rettime;
	}

	public void setRettime(int rettime) {
		this.rettime = rettime;
	}

	public int getUserrettime() {
		return userrettime;
	}

	public void setUserrettime(int userrettime) {
		this.userrettime = userrettime;
	}

	public int getBandwidth() {
		return bandwidth;
	}

	public void setBandwidth(int bandwidth) {
		this.bandwidth = bandwidth;
	}

	public int getUserbandwidth() {
		return userbandwidth;
	}

	public void setUserbandwidth(int userbandwidth) {
		this.userbandwidth = userbandwidth;
	}

	public int getHopcount() {
		return hopcount;
	}

	public void setHopcount(int hopcount) {
		this.hopcount = hopcount;
	}

	public int getUserhopcount() {
		return userhopcount;
	}

	public void setUserhopcount(int userhopcount) {
		this.userhopcount = userhopcount;
	}

	public boolean isEqualsa() {
		return equalsa;
	}

	public void setEqualsa(boolean equalsa) {
		this.equalsa = equalsa;
	}
	
}
