package application;

import java.net.URL;


public class ImageDeliverer {
	private static ImageDeliverer instance;
	public static ImageDeliverer getInstance() {
		if (ImageDeliverer.instance == null) {
			ImageDeliverer.instance = new ImageDeliverer();
		}
		return ImageDeliverer.instance;
	}
	private String str;
	private ImageDeliverer(){}


	public String getString(){
		return this.str;
	}

	public void storeStringImage(String str){
		this.str = str;

	}

	public void storeURLImage(URL url){
		this.str = url.toString();
	}

}
