package application;

import java.util.ArrayList;

public class ProxyList extends ArrayList<Proxy> {
	private static ProxyList instance;
	/**
	 * 
	 */
	private static final long serialVersionUID = 2105822771153959846L;
	public static ProxyList getInstance() {
		if (ProxyList.instance == null) {
			ProxyList.instance = new ProxyList();
		} 			
		return ProxyList.instance;

	}

	private ProxyList(){}


}
