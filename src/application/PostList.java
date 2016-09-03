package application;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PostList extends ArrayList<Post> {

	private static PostList instance;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static PostList getInstance () {
		if (PostList.instance == null) {
			PostList.instance = new PostList();
		}
		return PostList.instance;
	}

	public static void setInstance(PostList instance) {
		PostList.instance = instance;
	}

	private boolean clearOldPosts;

	private PostList(){}

	public boolean add(Post p) {
		boolean found = false;
		for (Post i : this) {
			if (i.getID() == p.getID()) {
				found = true;
			}
		}
		if (!found) {
			super.add(p);
			return true;
		} else return false;
	}

	public void clearList() {
		if (isClearOldPosts() && !PostList.getInstance().isEmpty()) {
			System.out.println("Posts in List before: " + this.size());
			int c = 0;
			Post OP = PostList.getInstance().get(0);
			Post last = PostList.getInstance().get(PostList.getInstance().size()-1);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			Date lastPostDate = null;
			try {
				lastPostDate = sdf.parse(last.getDate());
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			for (Post p : PostList.getInstance()) {
				Date postDate = null;
				try {
					postDate = sdf.parse(p.getDate());
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (lastPostDate.getTime() - postDate.getTime() > (5400000) ) {
					p.setMarkedForDeletion(true);
					c++;
				}
			} 
			PostList instance2 = new PostList();
			instance2.add(OP);
			for (Post p: this) {
				if (!p.isMarkedForDeletion()) {
					instance2.add(p);
				}
			}
			PostList.setInstance(null);
			System.gc();
			PostList.setInstance(instance2);
			instance2 = null;
			System.out.println("Cleared " + c + " posts!");
			System.gc();

		}
	}

	public String getList() {
		for(Post obj: this) {
			return obj.toString();
		}
		return "No entries left";
	}

	public boolean isClearOldPosts() {
		return clearOldPosts;
	}

	public void setClearOldPosts(boolean clearOldPosts) {
		this.clearOldPosts = clearOldPosts;
	}
}