package application;

import java.util.ArrayList;

public class myReplyList extends ArrayList<Reply> {
	private static myReplyList instance;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static synchronized myReplyList getInstance () {
		if (myReplyList.instance == null) {
			myReplyList.instance = new myReplyList();
		}
		return myReplyList.instance;
	}

	private myReplyList(){}

	public boolean add(Reply r) {
		boolean found = false;
		for (Reply i : this) {
			if (i.getNr() == r.getNr()) {
				found = true;
			}
		}
		if (!found) {
			super.add(r);
			return true;
		} else return false;
	}

	public String toString() {
		String ret = "";
		for (Reply r : this) {
			ret = ret + Integer.toString(r.getNr()) + ",";
		}
		return ret;

	}
}
