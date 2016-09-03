package application;

import java.util.ArrayList;

public class ReplyList extends ArrayList<Reply> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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

	public int[] toIntList() {
		int[] tr = new int[this.size()+1];
		tr[0] = 0;
		int i = 1;
		for (Reply r : this) {
			tr[i] = r.getNr();
			i++;
		}
		return tr;
	}

	public String toString() {
		String ret = "";
		for (Reply r : this) {
			ret = ret + Integer.toString(r.getNr()) + ",";
		}
		return ret;

	}
}
