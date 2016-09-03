package application;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class ProxyController {
	private String captcha_url = "https://8ch.net/dnsbls_bypass.php";
	@FXML
	private TextField challenge;
	private String[] img_coo;
	@FXML
	private ImageView iv_challenge;
	@FXML
	private Label lbl_proxy;
	@FXML
	private Label lbl_response;
	private ProxyList proxL;
	@FXML
	private Button send_challenge;
	@FXML
	private void challengeMe() throws ClientProtocolException, IOException {
		System.out.println(challenge.getText()+","+img_coo[1]);
		HttpResponse response = 
				Request.Post(captcha_url).bodyForm(
						Form.form().
						add("captcha_text", challenge.getText().trim()).
						add("captcha_cookie", img_coo[1].trim()).build()).
				addHeader("Host", "8ch.net").
				addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0").
				addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8").
				addHeader("Accept-Language", "de,en-US;q=0.7,en;q=0.3").
				addHeader("Content-Type", "application/x-www-form-urlencoded").
				addHeader("Connection", "keep-alive").
				addHeader("Referer", "https://8ch.net/dnsbls_bypass.php").execute().returnResponse();
		if (response.getStatusLine().getStatusCode() == 200) {
			lbl_response.setText("Success!!!");	 
		} else {
			lbl_response.setText("Error!");
		}

		getCaptcha();
	}

	@FXML
	private void exit() {
		Stage stage = (Stage)send_challenge.getScene().getWindow();
		stage.close();
	}

	@FXML
	private void getCaptcha() {
		try {
			URL chanCaptchaURL = new URL(captcha_url);
			getChallengeFromURL gcfu = new getChallengeFromURL(chanCaptchaURL);
			img_coo = gcfu.getString();
			iv_challenge.setImage(getImageFromBase64(img_coo[0]));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private Image getImageFromBase64(String base64) {
		byte[] imageByte;
		Base64.Decoder decoder = Base64.getDecoder();
		imageByte = decoder.decode(base64);
		ByteArrayInputStream in = new ByteArrayInputStream(imageByte);
		return new javafx.scene.image.Image(in);
	}

	@FXML
	private void initialize() {
		System.out.println("Hello proxy!");
		this.proxL = ProxyList.getInstance();
		for (Proxy p : proxL) {
			if (!p.getAdress().contains("http")) {
				lbl_proxy.setText("Your IP:" + p.getAdress());
			} else {
				lbl_proxy.setText("Proxy IP:" + p.getURL());
			}

		}
		getCaptcha();

	}

}
