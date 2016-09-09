package application;



public class HTMLParser {

	private GlobalFlags gfl;
	private myReplyList mrl;
	private PostList postList;

	public HTMLParser() {
		this.gfl = GlobalFlags.getInstance();
		postList = PostList.getInstance();
		mrl = myReplyList.getInstance();
	}

	public String parsePosts() throws NullPointerException {
		String ret = "<head><script language=\"javascript\" type=\"text/javascript\"> function toBottom(){window.scrollTo(0, document.body.scrollHeight);}"
				+ "function scrollToElement(id) {var elem = document.getElementById(id);var y = 0;  while (elem != null) {y += elem.offsetTop;elem = elem.offsetParent;}window.scrollTo(0, (y));}</script></head>";  
		ret = ret + "<body bgcolor=\"#d5daf0\" onload='toBottom()'>";
		if (!gfl.isHTMLWorking()){
			gfl.setHTMLWorking(true);
			for (Post p : postList) {
				boolean f = false;
				for (Reply r : mrl) {
					if (p.getID() == r.getNr()) f=true; 
				}
				ret = ret + "<p><b><i>ID:<font color=\"blue\"> <u><a class=\"postID\" id=\"" + p.getID() + "\" name=\"" + p.getID() + "\">" + p.getID() + "</a></u></b></i></font>";
				if (f) {
					ret = ret + "(You)";
				}
				ret = ret + " <b>" + p.getName() + "</b>" +  p.getEmail() + " ";
				ret = ret + "<img src=\"" + p.getFlag() + "\"/> ";
				if (!p.getFilename().equals("")) {
				ret = ret + " (" + p.getFilename() + ") ";}
				ret = ret + "<i>" + p.getDate() + "</i> ";
				if (p.getRepliedBy() != null) {
					String cc = "";
					for (Reply rr : p.getRepliedBy()) {
						cc = cc + "<a class=\"crosslink\" name=\"" + rr.getNr() + "\">>>" + rr.getNr() + "</a>";
					}
					ret = ret + cc;
				}
				ret = ret + "<br></p><p><table><tr><td>";
				if (p.getImage().equals("No file!")) {ret = ret + "</td><td>";} 
				else 
				{
					if (p.getActual().contains("gif"))
					{ret = ret + "<a><img class=\"image\" border=\"0\" width=\"200px\"  alt=\"" + p.getActual() + "\" src=\"" + p.getActual() + "\"></img></a></td><td>";} 
					else {ret = ret + "<a><img class=\"image\" border=\"0\" alt=\"" + p.getActual() + "\" src=\"" + p.getImage() + "\"></img></a></td><td>";
					}
					}
				ret = ret + "<br>" + p.getHTMLBody();
				ret = ret + "</td></tr></table></p><br><hr />";
			}
			gfl.setHTMLWorking(false);
		}
		return ret + "</body>";


	}

}
