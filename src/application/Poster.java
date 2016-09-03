package application;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Random;

import javax.imageio.ImageIO;

public class Poster {
	private static void extracted(File file) throws IOException {
		throw new IOException("Could not completely read file "+file.getName());
	} 
	private static long generateRandom(int length) {
		Random random = new Random();
		char[] digits = new char[length];
		digits[0] = (char) (random.nextInt(9) + '1');
		for (int i = 1; i < length; i++) {
			digits[i] = (char) (random.nextInt(10) + '0');
		}
		return Long.parseLong(new String(digits));
	}
	public static byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);
		long length = file.length();
		if (length > Integer.MAX_VALUE) {
		}
		byte[] bytes = new byte[(int)length];
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
			offset += numRead;
		}
		if (offset < bytes.length) {
			extracted(file);
		}
		is.close();
		return bytes;
	}
	private String b_address = "http://8ch.net/b/res/";
	private String boundary = "---------------------------";
	private BufferedReader br;
	private FileInputStream fileInputStream;
	private InputStreamReader inputStreamReader;
	private String lastResponse;
	private StringBuilder postData = new StringBuilder();
	private myReplyList rl;

	private String thread = "http://sys.8ch.net/post.php";

	private String threadID;

	private URL url = new URL(thread);

	public Poster(LinkedHashMap<Object, Object> params, String imagePath,String threadID) throws IOException {
		this.rl = myReplyList.getInstance();
		boundary = boundary + generateRandom(12); 
		lastResponse = "";
		this.threadID = threadID;
		for (Entry<Object, Object> param : params.entrySet()) {
			postData.append("--");
			postData.append(boundary);
			postData.append("\r\nContent-Disposition: form-data; name=\"");
			postData.append(param.getKey());
			postData.append("\"\r\n\r\n");
			postData.append(String.valueOf(param.getValue()));
			postData.append("\r\n");
		}
		postData.append("--");
		postData.append(boundary);
		postData.append("\r\nContent-Disposition: form-data; name=\"");
		postData.append("json_response");
		postData.append("\"\r\n\r\n");
		postData.append("1");
		postData.append("\r\n");
		postData.append("--");
		postData.append(boundary);
		postData.append("\r\nContent-Disposition: form-data; name=\"");
		postData.append("post");
		postData.append("\"\r\n\r\n");
		postData.append("New Reply");
		postData.append("\r\n");
		if (!imagePath.equals("")) { 
			try {
				ImageIO.read(new File(imagePath)); }
			catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("Error, this image won't do!");
			} finally {
				fileInputStream = new FileInputStream(new File(imagePath));
				inputStreamReader = new InputStreamReader((InputStream)fileInputStream, "ISO-8859-1");
				br = new BufferedReader(inputStreamReader);
				String imageData[] = new String[2]; //[0] = filename.ext [1]=ext converted to Type
				imageData = getImageData(imagePath);
				postData.append("--");
				postData.append(boundary);
				postData.append("\r\nContent-Disposition: form-data; name=\"file\"; filename=\"");
				postData.append(imageData[0]);
				postData.append("\"\r\n");
				postData.append("Content-Type: image/");
				postData.append(imageData[1]);
				postData.append("\r\n\r\n");
				int c;
				while((c = br.read()) != -1) {
					char character = (char) c;
					postData.append(character);
				}
				postData.append("\r\n");
			}
		}

		postData.append("--" + boundary + "--\r\n");
		final byte[] postDataBytes = postData.toString().getBytes("ISO-8859-1");
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					post(postDataBytes);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		t.start();

	}

	public byte[] extractBytes (BufferedImage image, String ext) throws IOException {
		ByteArrayOutputStream s = new ByteArrayOutputStream();
		ImageIO.write(image, ext, s);
		byte[] res  = s.toByteArray();
		return res;
	}

	private String[] getImageData(String imagePath) {
		Path p = Paths.get(imagePath);
		String fileName = p.getFileName().toString();
		String ext = "";
		String[] toReturn = new String[2];
		int mid = fileName.lastIndexOf(".");
		toReturn[0] = fileName;
		ext = fileName.substring(mid + 1, fileName.length());
		if (ext.equalsIgnoreCase("jpg")) { toReturn[1] = "jpeg";} else
			if (ext.equalsIgnoreCase("jpeg")) { toReturn[1] = "jpeg";} else
				if (ext.equalsIgnoreCase("png")) { toReturn[1] = "png";} else
					if (ext.equalsIgnoreCase("gif")) { toReturn[1] = "gif";} else
					{toReturn[1] = "none";}
		return toReturn;
	}	

	public String getLastResponse() {
		return lastResponse;
	}

	private String parseResponse(String response) {
		String tr = "";
		if (!response.equals("")){
			String re4="#(\\d+){7}";
			Pattern pa = Pattern.compile(re4,Pattern.CASE_INSENSITIVE);
			Matcher m = pa.matcher(response);
			if (m.find()) {
				String int1=m.group();
				tr = int1.replaceAll("#", "");
			}}
		return tr;
	}

	private synchronized void post(byte[] postDataBytes) throws IOException, InterruptedException {
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setDefaultUseCaches(true);
		conn.setReadTimeout(0);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
		conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
		conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
		conn.setRequestProperty("Host", "8ch.net");
		conn.setRequestProperty("Connection", "keep-alive");
		conn.setRequestProperty("Referer", b_address + threadID + ".html");
		conn.setRequestProperty("Pragma", "no-cache");
		conn.setRequestProperty("Cache-Control", "no-cache");
		System.out.println("Trying to post....");
		conn.getOutputStream().write(postDataBytes,0,postDataBytes.length);
		try {
			System.out.println(conn.getResponseMessage().toString());
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "ISO-8859-1"));
			String response = "";
			for (int c; (c = in.read()) >= 0; response = response + ((char)c));
			this.lastResponse=response;
			if (!response.equals("")){rl.add(new Reply(parseResponse(response)));};
		} catch (IOException e) {
			System.out.println("http://8ch.net/post.php had a Gateway Timeout");
			conn.disconnect();
			System.out.println("Trying again!");
			post(postDataBytes);
		}
		conn.disconnect();
		return;
	}
}
