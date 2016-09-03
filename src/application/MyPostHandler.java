package application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MyPostHandler {
	private static String Filename = "replylist.csv";
	private myReplyList myPostList;
	private String threadID;
	private String tmp_file;

	public MyPostHandler() {
		this.myPostList = myReplyList.getInstance();
		String os = System.getProperty("os.name").toLowerCase();
		if (os.equals("linux")) {tmp_file = "/tmp/";} else
		{tmp_file = "C:\\Temp\\";}
		tmp_file = tmp_file.replaceAll("\\\\", "/");
	}

	public String getThreadID() {
		return this.threadID;
	}

	public void giveThreadID(String ID) {
		this.threadID = ID;
	}

	public void loadList() {
		File fi = new File(tmp_file+Filename);
		if (fi.exists()){
			Path f = Paths.get(tmp_file+Filename);
			try {

				String content = new String(Files.readAllBytes(f));
				String[] split = content.split(";");
				this.threadID = split[0];
				for (int i = 1; i < split.length; i++) {
					myPostList.add(new Reply(split[i]));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void saveList() throws IOException {
		String content = "";
		if (myReplyList.getInstance()==null) {System.out.println("Error: No own replies");} else {
			if (!this.threadID.equals("")) {
				content = "" + threadID + ";"; } 
			else {
				content = "0000000;";
			}
			for (Reply r : myPostList) {
				content = content + r.getNr() + ";";
			}
			FileWriter fw = new FileWriter(new File(tmp_file+Filename));
			fw.write(content);
			fw.close();
		}

	}

}
