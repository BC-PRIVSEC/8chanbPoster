package application;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.imageio.ImageIO;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ImageController {
	@FXML
	private Pane bot;
	@FXML
	private Button btn_close;
	@FXML
	private ImageView imageView;
	private ImageDeliverer imgD;
	private Stage stage;
	private String str;
	@FXML
	private Pane up;

	@FXML
	private void exit() {
		stage = (Stage)btn_close.getScene().getWindow();
		stage.close();
	}
	@FXML
	public void initialize() {
		imgD = ImageDeliverer.getInstance();
		str = imgD.getString();
		str = replaceHTTP(str);
		Task<Void> sleeper = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
				return null;
			}
		};
		sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			public void handle(WorkerStateEvent event) {
				try {
					setStuff();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		new Thread(sleeper).start();

	}

	private String replaceHTTP(String str2) {
		return str.replace("https", "http");
	}
	private synchronized void setStuff() throws IOException {
		stage = (Stage)btn_close.getScene().getWindow();
		URL url = new URL(str);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoInput(true);
		conn.setDefaultUseCaches(true);
		conn.setReadTimeout(0);
		conn.setRequestMethod("GET");
		conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.3; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0");
		conn.setRequestProperty("Host", "8ch.net");
		conn.setRequestProperty("Connection", "keep-alive");
		conn.setRequestProperty("Referer", "http://media2.8ch.net/b/src/");
		conn.setRequestProperty("Pragma", "no-cache");
		conn.setRequestProperty("Cache-Control", "no-cache");
		int responseCode = conn.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			InputStream inputStream = conn.getInputStream();
			BufferedImage bf = null;

			try {
				bf = ImageIO.read(inputStream);
			} catch (IOException e) {
				System.out.println("Propably GIF Error!");
			}
			WritableImage wr = null;
			if (bf != null) {
				wr = new WritableImage(bf.getWidth(), bf.getHeight());
				PixelWriter pw = wr.getPixelWriter();
				for (int x = 0; x < bf.getWidth(); x++) {
					for (int y = 0; y < bf.getHeight(); y++) {
						pw.setArgb(x, y, bf.getRGB(x, y));
					}
				}
			}
			imageView.setImage(wr);
			double width = imageView.getImage().getWidth();
			double height = imageView.getImage().getHeight();
			if (width>1500) {width=width/2;height=height/2;}
			if (height>900) {height=height/2;width=width/2;}
			imageView.setPreserveRatio(true);
			imageView.setFitHeight(height);
			imageView.setFitWidth(width);
			stage.setWidth(imageView.getFitWidth()+20);
			stage.setHeight(imageView.getFitHeight()+35);
			bot.setLayoutY(imageView.getFitHeight());
		}
		{
			System.out.println("NOT OK:" + conn.getResponseCode());

		}
		conn.disconnect();
	}

}
