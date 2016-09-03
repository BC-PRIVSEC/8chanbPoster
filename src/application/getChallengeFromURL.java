package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/*
 * JSOUP https://jsoup.org/
 * is distributed under the MIT Licence
 */
public class getChallengeFromURL {
	private URL url;

	public getChallengeFromURL(URL url) {
		this.url = url;
	}

	public String[] getString() {
		InputStream is = null;
		Document doc;
		BufferedReader br;
		String line;
		String document = "";
		String toReturn[] = {"",""};
		try {
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setDefaultUseCaches(true);
			conn.setReadTimeout(0);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			conn.setRequestProperty("Host", "8ch.net");
			conn.setRequestProperty("Connection", "keep-alive");
			conn.setRequestProperty("Referer", "https://8ch.net/dnsbls_bypass.php");
			conn.setRequestProperty("Pragma", "no-cache");
			conn.setRequestProperty("Cache-Control", "no-cache");
			br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			while ((line = br.readLine()) != null) {
				document = line;
			}
			conn.disconnect();
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (is != null) is.close();
			} catch (IOException ioe) {
				// nothing to see here
			}
		}
		doc = Jsoup.parse(document);
		// try for success
		Elements posts = doc.getElementsByTag("img");
		for (Element p : posts) {
			Elements t = p.getElementsByAttribute("src");
			for (Element e : t) {
				if (e.attr("src").contains("data:image/png")) {
					toReturn[0] = e.attr("src").substring(e.attr("src").indexOf(",")+1);
				}
			}
		}
		posts = doc.getElementsByClass("captcha_cookie");
		for (Element p : posts) {
			toReturn[1] = p.attr("value");
		}
		return toReturn;
	}
}
