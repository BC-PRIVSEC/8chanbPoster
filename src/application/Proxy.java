package application;

public class Proxy {
	private String adress;
	private int port;

	public Proxy(String adress) {
		this.setAdress(adress);
		this.setPort(0);

	}

	public Proxy(String adress, int port) {
		this.setAdress(adress);
		this.setPort(port);

	}

	public String getAdress() {
		return adress;
	}

	public int getPort() {
		return port;
	}

	public String getURL() {
		return adress+":"+port;
	}

	public void setAdress(String adress) {
		this.adress = adress;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
