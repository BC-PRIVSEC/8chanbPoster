package application;

public class Reply {
	private int nr;

	public Reply(int nr) {
		this.nr = nr;
	}

	public Reply(String nr) {
		if (!nr.equals("")) this.nr = Integer.parseInt(nr);
	}

	public int getNr() {
		return nr;
	}
}
