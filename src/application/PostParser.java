package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/*
 * JSOUP https://jsoup.org/
 * is distributed under the MIT Licence
 */
public class PostParser {
	private String b_address = "http://8ch.net/b/res/";
	private boolean cop;
	private Document doc;
	private GlobalFlags gfl;
	private myReplyList myPostIDs;
	private PostList myPosts;
	private String turl;

	public PostParser() {
		setCop(false);
		this.gfl = GlobalFlags.getInstance();
		gfl.setParserWorking(false);
		myPosts = PostList.getInstance();
		myPostIDs = myReplyList.getInstance();

	}

	public PostList getPostList() {
		return myPosts;
	}

	public boolean isCop() {
		return cop;
	}

	public void parse(String threadID) throws NullPointerException {
		if (!gfl.isParserWorking()) {
			gfl.setParserWorking(true);
			if (cop) myPosts.setClearOldPosts(true);
			if (!cop) myPosts.setClearOldPosts(false);
			turl = b_address + threadID + ".html";
			URL url;
			InputStream is = null;
			BufferedReader br;
			String line;
			String document = "";
			try {
				url = new URL(turl);
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setDefaultUseCaches(true);
				conn.setReadTimeout(0);
				conn.setRequestMethod("GET");
				conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.3; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0");
				conn.setRequestProperty("Host", "8ch.net");
				conn.setRequestProperty("Connection", "keep-alive");
				conn.setRequestProperty("Referer", b_address + threadID + ".html");
				conn.setRequestProperty("Pragma", "no-cache");
				conn.setRequestProperty("Cache-Control", "no-cache");
				br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

				while ((line = br.readLine()) != null) {
					document += new String(line.getBytes(),"UTF-8");
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
			Elements posts = doc.getElementsByClass("post");
			doc = null;
			System.out.print("got " + posts.size() + " posts!\n" );
			for (Element post : posts) {
				String body = "";
				String id = "";
				String file = "";
				String date = "";
				String name = "";
				String email = "";
				ReplyList repliedTo = new ReplyList();
				Elements postbody = post.getElementsByClass("body-line");
				for (Element p : postbody) {
					String re3="(&gt;)";
					String q ="^(&gt;)((?:[a-z][a-z0-9_]*)|$)";
					String re4="(\\d){7}";
					Pattern pa = Pattern.compile(re3+re4,Pattern.DOTALL);
					Pattern pq = Pattern.compile(q,Pattern.DOTALL);
					Matcher mq = pq.matcher(p.html());
					Matcher m = pa.matcher(p.html());
					if (m.find()) {
						String int1=m.group();
						String[] strip = int1.split(";");
						Reply reply = new Reply(strip[1]);
						int1 = strip[1];
						boolean f = false;
						for (Reply r : myPostIDs)
						{
							if (reply.getNr() == r.getNr()) {f = true;
							}
						}
						if (f) {
							body = body + "<font color=\"ff0066\"><b><a class=\"crosslink\" name=\""+int1+ "\">>>" + int1 + "<i>(You)</i></a></b></font>";

						}
						else
						{
							body = body + "<a class=\"crosslink\" name=\""+int1+ "\">>>"  + int1 + "</a>";

						}
						repliedTo.add(reply);
						p.empty();
					}
					if (mq.find()) {
						body = body + "<font color=\"789922\">" + p.html() + "</font>";
						p.empty();
					}
					Elements links = p.getElementsByTag("a");
					for (Element a : links) {
						Elements rel = a.getElementsByAttribute("rel");
						for (Element r : rel) {
							r.removeAttr("href");
						}
						rel = null;
					}
					links = null; 
					
					body = body + p.html() + "\n";
				}
				postbody = null;
				Elements ids = post.getElementsByClass("post_no");
				id = ids.get(1).ownText();
				ids = null;
				Elements names = post.getElementsByClass("name");
				Elements trips = post.getElementsByClass("trip");
				if (trips.first() != null) {name = names.first().ownText() +" "+ trips.first().ownText();}
				else {name = names.first().ownText();}
				names = null;
				trips = null;
				Elements time = post.getElementsByTag("time");
				date = time.attr("datetime");
				time = null;
				Elements files = post.getElementsByClass("post-image");
				if (files.first() != null) {
					for (Element f : files) {
						Elements a = f.getElementsByTag("img");
						file = a.first().attr("src");
						if (file.contains("deleted.png")) {
							file = "No file!";
						}
						a = null;
					}
				} else {file = "No file!";}
				files = null;
				Elements images = post.getElementsByTag("a");
				String actual ="";
				for (Element d : images) {
					Elements f = d.getElementsByAttribute("target");
					for (Element d2: f) {
						if (d2.hasAttr("href")) {
							actual = d2.attr("href");
							if (actual.contains("jpeg") || actual.contains("jpg") || actual.contains("png")|| actual.contains("gif")) {
								actual = d2.attr("href");
							}
						}
					}
				}
				images = null;
				Elements emails = post.getElementsByClass("email");
				if (emails.first() != null) {
					for (Element e : emails) {
						Elements a = e.getElementsByTag("a");
						email = a.first().attr("href");
						if (email.contains("email-protection")) {
							email = "";
						}
						a = null;
					}
				} else {email = "";}
				emails = null;
				Elements flags = post.getElementsByClass("flag");
				String flag = "";
				for (Element f : flags) {
					Elements img = f.getElementsByTag("img");
					if (!img.first().attr("src").equals("")) {
						flag = "http://8ch.net" + img.first().attr("src");}
				}
				Post post_file = new Post("",name,body,email,id,date,file,repliedTo,flag,actual,false);
				myPosts.add(post_file);
				post_file = null;
			}
			System.out.println("Finished parsing posts!");
			posts = null;
			myPosts.clearList();
			setCrosslinks();
			gfl.setParserWorking(false);
			System.gc();
		}

	}

	public String postListToString() {
		return myPosts.toString();
	}

	public void setCop(boolean cop) {
		this.cop = cop;
	}

	private void setCrosslinks() {
		for (Post p : myPosts) {
			int[] replies = p.getRepliesInt();
			for (Post p2 : myPosts) {
				for (int i = 0; i < replies.length; i++)
					if (replies[i] != p.getID()) {
						if (replies[i] == p2.getID()) {
							p2.addRepliedBy(new Reply (p.getID()));
						}
					}
			}
		}

	}

}
