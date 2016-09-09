package application;

public class Post {
	private String actualImage;
	private String body;
	private String date;
	private String email;
	private String flag;
	private int ID;
	private String image;
	private boolean markedForDeletion;
	private String poster;
	private ReplyList repliedby;
	private ReplyList repliedTo;
	private String subject;
	private String filename;


	public Post (String subject, String name, String body,String email_in, String ID, String date, String image, String filename, ReplyList replyList,String sflag,String actualImage, boolean flag)  {
		this.body = body;
		this.setSubject(subject);
		this.poster = name;
		this.ID = Integer.parseInt(ID);
		this.image = image;
		this.date = date;
		this.setFilename(filename);
		this.email = email_in;
		this.setMarkedForDeletion(flag);
		if (!email.equals("")) {
			if (email.contains("@") && !email.contains(":")) {
				// nothing!
			} else {
				String[] splitter = email.split(":");
				email = splitter[1];}
		}
		this.repliedTo = replyList;
		this.repliedby = new ReplyList();
		this.flag = sflag;
		this.actualImage = actualImage;
	}

	public void addFlag(String s) {
		this.flag = s;
	}

	public void addRepliedBy(Reply r) {
		repliedby.add(r);
	}

	public String getActual() {
		return actualImage;
	}

	public String getBody() {
		return body;
	}

	public String getDate() {
		return date;
	}

	public String getEmail() {
		return email;
	}

	public String getFlag() {
		return this.flag;
	}

	public String getHTMLBody() {
		String rt = body;
		rt = rt.replaceAll("(\r\n|\n)", "<br/>");
		return rt;
	}
	public int getID() {
		return ID;
	}
	public String getImage() {
		return image;
	}
	public String getLinkReplies() {
		String ret = "";
		for (Reply l : repliedTo ) {
			ret = ret + "<a href=\"#" + l.getNr() + "\">" + l.getNr() + "</a>,";
		}
		return ret;
	}

	public String getName() {
		return poster;
	}

	public ReplyList getRepliedBy() {
		return repliedby;
	}
	public ReplyList getReplies() {
		return repliedTo;
	}

	public int[] getRepliesInt() {
		return repliedTo.toIntList();
	}

	public String getSubject() {
		return subject;
	}

	public boolean isMarkedForDeletion() {
		return markedForDeletion;
	}

	public void setMarkedForDeletion(boolean markedForDeletion) {
		this.markedForDeletion = markedForDeletion;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String toString() {
		return "Post ID: " + ID + "\nPoster: " + poster + "\nEmail:" + email +  "\nDate: " + date + "\nRepliedTo:" + repliedTo.toString() + "\nBody:" + body + "\n##########################\n";
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
}
